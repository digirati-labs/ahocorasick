package com.digirati.labs.ahocorasick.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class StringIndexGraph<Id> {

    private final List<Vertex> vertices = new CopyOnWriteArrayList<>();

    public StringIndexGraph() {
        vertices.add(new Vertex(-1, '$'));
    }

    public int getEdge(int edgeId, char ch) {
        int c = ch - 'a';

        Vertex v = vertices.get(edgeId);
        return v.edges.get(c);
    }

    public int getNodeAncestor(int edgeId) {
        Vertex v = vertices.get(edgeId);
        return v.prevState;
    }

    public char getNodeAncestorCharacter(int edgeId) {
        Vertex v = vertices.get(edgeId);
        return v.prevChar;
    }

    public boolean isLeafNode(int nodeId) {
        Vertex v = vertices.get(nodeId);
        return v.leaf;
    }

    public Set<Id> getNodeLabels(int nodeId) {
        Vertex v = vertices.get(nodeId);
        return new HashSet<>(v.ids);
    }

    public int addEdge(int from, char ch) {
        int c = ch - 'a';

        Vertex fromV = vertices.get(from);
        Vertex to = new Vertex(from, ch);

        int toId;
        synchronized (vertices) {
            toId = vertices.size();
            vertices.add(to);
        }

        fromV.edges.set(c, toId);
        return toId;
    }

    public void removeNodeLabel(int node, Id labelId) {
        Vertex v = vertices.get(node);
        v.ids.remove(labelId);
        v.leaf = !v.ids.isEmpty();
    }

    public void addNodeLabel(int edgeId, Id labelId) {
        Vertex edge = vertices.get(edgeId);
        edge.ids.add(labelId);
        edge.leaf = true;
    }

    class Vertex {

        /**
         * A cached transition table that takes into account suffix links in the trie.
         */
        public final AtomicIntegerArray transitions = new AtomicIntegerArray(new int[26]);
        /**
         * The indices of each edge in the trie for each character in the alphabet.
         */
        public final AtomicIntegerArray edges = new AtomicIntegerArray(new int[26]);
        /**
         * The index of the previous state that lead to this vertex.
         */
        public final int prevState;
        /**
         * The character of the previous state that lead for this vertex.
         */
        public final char prevChar;
        /**
         * Memoized value for a suffix link.
         */
        public final AtomicInteger link = new AtomicInteger(-1);
        /**
         * A set of identifiers associated with this node.
         */
        public final Set<Id> ids = new CopyOnWriteArraySet<>();
        /**
         * If this vertex represents an edge, or a leaf with an associated term.
         */
        boolean leaf = false;

        private Vertex(int prevState, char prevChar) {
            this.prevState = prevState;
            this.prevChar = prevChar;

            for (int i = 0; i < 26; i++) {
                edges.set(i, -1);
                transitions.set(i, -1);
            }
        }
    }
}
