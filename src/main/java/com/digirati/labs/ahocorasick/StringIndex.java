package com.digirati.labs.ahocorasick;

import com.digirati.labs.ahocorasick.graph.StringIndexGraph;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An
 */
public class StringIndex<Id> {

    private final StringIndexGraph<Id> graph = new StringIndexGraph<>();
    private final Map<Integer, Integer> edgeSuffixLinks = new ConcurrentHashMap<>();
    private final Map<Long, Integer> transitionCache = new ConcurrentHashMap<>();

    public void add(Id id, String value) {
        int v = 0;

        for (char ch : value.toCharArray()) {
            int edge = graph.getEdge(v, ch);
            if (edge == -1) {
                v = graph.addEdge(v, ch);
            } else {
                v = edge;
            }
        }

        graph.addNodeLabel(v, id);
    }

    public void remove(Id id, String value) {
        int v = 0;

        for (char ch : value.toCharArray()) {
            v = graph.getEdge(v, ch);
            if (v == -1) {
                throw new IllegalArgumentException("\"" + value + "\" does not exist in the index");
            }
        }

        graph.removeNodeLabel(v, id);
    }

    /**
     * Produce a stream of all strings within this {@code StringIndex} that occur in the {@code input} text.
     */
    public Stream<StringIndexMatch<Id>> match(CharSequence input) {
        return StreamSupport.stream(new StringIndexSpliterator<>(this, input), false);
    }

    public boolean find(String input) {
        return match(input).count() > 0;
    }


    private int getSuffixLink(int currentState) {
        return edgeSuffixLinks.computeIfAbsent(currentState, node -> {
            if (node == 0 || graph.getNodeAncestor(node) == 0) {
                return 0;
            } else {
                return transition(getSuffixLink(graph.getNodeAncestor(node)), graph.getNodeAncestorCharacter(node));
            }
        });
    }

    Set<Id> labels(int currentState) {
        return !graph.isLeafNode(currentState) ? Collections.emptySet() : graph.getNodeLabels(currentState);
    }

    int transition(int currentState, char currentChar) {
        long key = (long) currentState << 32 | currentChar & 0xFFFFFFFFL;

        if (transitionCache.containsKey(key)) {
            return transitionCache.get(key);
        }

        int transition = graph.getEdge(currentState, currentChar);
        if (transition == -1) {
            if (currentState == 0) {
                return 0;
            }

            transition = transition(getSuffixLink(currentState), currentChar);
        }

        transitionCache.put(key, transition);
        return transition;
    }

}
