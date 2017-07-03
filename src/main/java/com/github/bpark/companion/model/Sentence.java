package com.github.bpark.companion.model;

import java.util.List;

/**
 * @author ksr
 */
public class Sentence {

    private List<AnalyzedWord> analyzedWords;

    public Sentence(List<AnalyzedWord> analyzedWords) {
        this.analyzedWords = analyzedWords;
    }

    public List<AnalyzedWord> getAnalyzedWords() {
        return analyzedWords;
    }
}
