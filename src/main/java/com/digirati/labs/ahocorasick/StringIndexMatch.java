package com.digirati.labs.ahocorasick;

import java.util.Set;

public class StringIndexMatch<Id> {

    private final Set<Id> id;
    private final int position;

    public StringIndexMatch(Set<Id> id, int position) {
        this.id = id;
        this.position = position;
    }
}
