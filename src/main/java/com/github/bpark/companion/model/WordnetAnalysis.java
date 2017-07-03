package com.github.bpark.companion.model;

import java.util.List;

public class WordnetAnalysis {

    private List<Sentence> sentences;

    public WordnetAnalysis(List<Sentence> sentences) {
        this.sentences = sentences;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }
}
