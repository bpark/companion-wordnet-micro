/*
 * Copyright 2017 Kurt Sparber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bpark.companion;


import com.github.bpark.companion.input.AnalyzedText;
import com.github.bpark.companion.model.AnalyzedWord;
import com.github.bpark.companion.model.PosType;
import com.github.bpark.companion.model.Sentence;
import com.github.bpark.companion.model.WordnetAnalysis;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.morph.WordnetStemmer;
import io.vertx.core.json.Json;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Single;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ksr
 */
public class WordnetVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(WordnetVerticle.class);

    private static final String ADDRESS = "wordnet.analysis";

    private static final String NLP_KEY = "nlp";
    private static final String WORDNET_KEY = "wordnet";

    private Dictionary dictionary;

    @Override
    public void start() throws Exception {

        logger.info("starting wordnet verticle");

        vertx.<Void>rxExecuteBlocking(future -> {

            try {

                loadDictionary();

                future.complete();

            } catch (IOException e) {
                future.fail(e);
            }

        }).toObservable().subscribe(
                success -> registerAnalyzer(),
                error -> logger.error("faild to load dictionary!", error)
        );

    }

    private void loadDictionary() throws IOException {
        dictionary = new Dictionary(new File("/wordnet/dict"));
        dictionary.open();
        //dictionary.load();
    }

    private void registerAnalyzer() {
        EventBus eventBus = vertx.eventBus();

        MessageConsumer<String> consumer = eventBus.consumer(ADDRESS);
        Observable<Message<String>> observable = consumer.toObservable();
        observable.subscribe(message -> {

            String id = message.body();

            readMessage(id).flatMap(analyzedText -> {

                List<Sentence> analyses = new ArrayList<>();

                analyzedText.getSentences().forEach(taggedText -> {
                    List<AnalyzedWord> analyzedWords =
                            taggedText.zip().map(a -> analyzeWord(a.getA(), a.getB())).collect(Collectors.toList());

                    analyses.add(new Sentence(analyzedWords));
                });

                return Observable.just(analyses);
            }).flatMap(analyses -> saveMessage(id, analyses)).subscribe(a -> message.reply(id));

        });
    }

    private AnalyzedWord analyzeWord(String word, String posTag) {

        AnalyzedWord analyzedWord = null;

        POS pos = PosType.byPennTag(posTag);

        logger.info("word: {}, pennTag: {}", word, posTag);

        if (pos != null) {

            logger.info("evaluated pos: {}", pos.name());

            String stem = findStem(word, pos);

            logger.info("stem: {}", stem);

            IIndexWord idxWord = dictionary.getIndexWord(stem, pos);

            if (idxWord != null && idxWord.getWordIDs() != null && idxWord.getWordIDs().size() > 0) {

                IWordID wordID = idxWord.getWordIDs().get(0); // 1st meaning
                IWord dictionaryWord = dictionary.getWord(wordID);
                ISynset synset = dictionaryWord.getSynset();

                List<String> synonyms = synset.getWords().stream().map(IWord::getLemma).collect(Collectors.toList());

                List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);

                List<String> hypernymWords = hypernyms.stream()
                        .map(h -> dictionary.getSynset(h).getWords())
                        .flatMap(Collection::stream)
                        .map(IWord::getLemma)
                        .collect(Collectors.toList());

                analyzedWord = new AnalyzedWord();
                analyzedWord.setGloss(synset.getGloss());
                analyzedWord.setStem(stem);
                analyzedWord.setHypernyms(hypernymWords);
                analyzedWord.setLemma(dictionaryWord.getLemma());
                analyzedWord.setLexicalName(synset.getLexicalFile().getName());
                analyzedWord.setLexialDescription(synset.getLexicalFile().getDescription());
                analyzedWord.setSynonyms(synonyms);
            }
        }

        return analyzedWord;
    }

    private String findStem(String word, POS pos) {
        WordnetStemmer stemmer = new WordnetStemmer(dictionary);
        List<String> stems = stemmer.findStems(word, pos);

        return stems != null && stems.size() > 0 ? stems.get(0) : word;
    }

    private Observable<AnalyzedText> readMessage(String id) {
        return vertx.sharedData().<String, String>rxGetClusterWideMap(id)
                .flatMap(map -> map.rxGet(NLP_KEY))
                .flatMap(content -> Single.just(Json.decodeValue(content, AnalyzedText.class)))
                .toObservable();
    }

    private Observable<Void> saveMessage(String id, List<Sentence> analyses) {
        return vertx.sharedData().<String, String>rxGetClusterWideMap(id)
                .flatMap(map -> map.rxPut(WORDNET_KEY, Json.encode(new WordnetAnalysis(analyses))))
                .toObservable();
    }
}
