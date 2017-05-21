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


import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.*;
import io.vertx.core.json.JsonArray;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ksr
 */
public class WordnetVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(WordnetVerticle.class);

    private RAMDictionary dictionary;

    @Override
    public void start() throws Exception {

        logger.info("starting wordnet verticle");

        loadDictionary();
        registerSynonyms();

    }

    private void loadDictionary() throws IOException {
        dictionary = new RAMDictionary(new File("/wordnet/dict"), ILoadPolicy.NO_LOAD);
        dictionary.open();
        dictionary.load();
    }

    private void registerSynonyms() {
        EventBus eventBus = vertx.eventBus();

        MessageConsumer<String> consumer = eventBus.consumer(WordnetAddresses.SYNONYMS.getAddress());
        Observable<Message<String>> observable = consumer.toObservable();
        observable.subscribe(message -> {
            String body = message.body();

            IIndexWord idxWord = dictionary.getIndexWord(body, POS.NOUN);
            IWordID wordID = idxWord.getWordIDs().get(0); // 1st meaning
            IWord word = dictionary.getWord(wordID);
            ISynset synset = word.getSynset();

            List<String> synonyms = synset.getWords().stream().map(IWord::getLemma).collect(Collectors.toList());

            message.reply(new JsonArray(synonyms));
        });

    }

}
