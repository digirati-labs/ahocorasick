package com.digirati.labs.ahocorasick;

import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public class StringIndexSpliterator<Id> extends Spliterators.AbstractSpliterator<StringIndexMatch<Id>> {

    /**
     * The index used for the scan.
     */
    private final StringIndex<Id> index;

    /**
     * The input to scan for matches.
     */
    private final CharSequence input;

    /**
     * The current position within the input text;
     */
    private int position = 0;

    /**
     * The current state within the {@link StringIndex}.
     */
    private int state = 0;

    StringIndexSpliterator(StringIndex<Id> index, CharSequence input) {
        super(Long.MAX_VALUE, Spliterator.ORDERED);
        this.index = index;
        this.input = input;
    }

    @Override
    public boolean tryAdvance(Consumer<? super StringIndexMatch<Id>> action) {
        while (position < input.length()) {
            state = index.transition(state, input.charAt(position++));

            Set<Id> labels = index.labels(state);
            if (!labels.isEmpty()) {
                action.accept(new StringIndexMatch<>(labels, position));
                return true;
            }
        }

        return false;
    }
}
