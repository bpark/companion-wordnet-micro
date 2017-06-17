package com.github.bpark.companion.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaggedText {

    private List<String> tokens;

    private List<String> posTags;

    public List<String> getTokens() {
        return tokens;
    }

    public List<String> getPosTags() {
        return posTags;
    }

    @JsonIgnore
    public Stream<Pair<String, String>> zip() {
        Iterator<String> i = tokens.iterator();
        return posTags.stream().filter(x -> i.hasNext()).map(b -> new Pair<>(i.next(), b));
    }
}
