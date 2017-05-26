package com.github.bpark.companion.model;

import edu.mit.jwi.item.POS;

import java.util.Arrays;
import java.util.List;

public enum PosType {

    NOUN(POS.NOUN, Arrays.asList("NN", "NNS", "NNP", "NNPS", "UH")),
    VERB(POS.VERB, Arrays.asList("VB", "VBD", "VBG", "VBN", "VBP", "VBZ")),
    ADVERB(POS.ADVERB, Arrays.asList("RB", "RBR", "RBS")),
    ADJECTIVE(POS.ADJECTIVE, Arrays.asList("JJ", "JJR", "JJS"));

    private List<String> pennTags;
    private POS pos;

    PosType(POS pos, List<String> pennTags) {
        this.pos = pos;
        this.pennTags = pennTags;
    }

    public static POS byPennTag(String pennTag) {
        PosType[] values = PosType.values();

        for (PosType value : values) {
            if (value.pennTags.contains(pennTag)) {
                return value.pos;
            }
        }

        return null;
    }

}
