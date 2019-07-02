package com.digirati.labs.ahocorasick;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import java.util.UUID;
import org.junit.Test;

public class StringIndexTest {

    @Test
    public void find_ReturnsAddedString() {
        StringIndex<UUID> index = new StringIndex<>();

        index.add(UUID.randomUUID(), "hello");
        assertTrue("Term should be found in index", index.find("hello"));
    }

    @Test
    public void find_DoesntReturnRemovedString() {
        StringIndex<UUID> index = new StringIndex<>();

        UUID id = UUID.randomUUID();

        index.add(id, "hello");
        assumeTrue(index.find("hello"));

        index.remove(id, "hello");
        assertFalse("Term shouldnt be found in index", index.find("hello"));
    }
}