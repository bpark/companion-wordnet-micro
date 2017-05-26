package com.github.bpark.companion.model;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class TaggedText {

    private List<String> tokens;

    private List<String> posTags;

    public List<String> getTokens() {
        return tokens;
    }

    public List<String> getPosTags() {
        return posTags;
    }

    public  Stream<Pair<String, String>> zip()
    {
        Iterator<String> i=tokens.iterator();
        return posTags.stream().filter(x->i.hasNext()).map(b->new Pair<>(i.next(), b));
    }
}
