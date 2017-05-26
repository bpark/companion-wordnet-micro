package com.github.bpark.companion.model;

import java.util.List;

public class WordnetAnalysis {

    private List<AnalyzedWord> analyzedWords;

    public WordnetAnalysis(List<AnalyzedWord> analyzedWords) {
        this.analyzedWords = analyzedWords;
    }

    public List<AnalyzedWord> getAnalyzedWords() {
        return analyzedWords;
    }
}
