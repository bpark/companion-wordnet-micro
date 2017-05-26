package com.github.bpark.companion.model;

import java.util.List;

public class AnalyzedWord {

    private String stem;

    private String lemma;

    private String gloss;

    private String lexicalName;

    private String lexialDescription;

    private List<String> synonyms;

    private List<String> hypernyms;


    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getGloss() {
        return gloss;
    }

    public void setGloss(String gloss) {
        this.gloss = gloss;
    }

    public String getLexicalName() {
        return lexicalName;
    }

    public void setLexicalName(String lexicalName) {
        this.lexicalName = lexicalName;
    }

    public String getLexialDescription() {
        return lexialDescription;
    }

    public void setLexialDescription(String lexialDescription) {
        this.lexialDescription = lexialDescription;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public List<String> getHypernyms() {
        return hypernyms;
    }

    public void setHypernyms(List<String> hypernyms) {
        this.hypernyms = hypernyms;
    }
}
