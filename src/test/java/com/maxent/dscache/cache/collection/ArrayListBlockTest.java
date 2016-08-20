package com.maxent.dscache.cache.collection;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by alain on 16/8/16.
 */
public class ArrayListBlockTest {
    @Test
    public void iterator() throws Exception {
        ArrayListBlock<String> arrayListBlock = new ArrayListBlock<>(0);
        arrayListBlock.add("hello");
        arrayListBlock.add("world");

        for (String elem : arrayListBlock) {
            System.out.println(elem);
        }

        TestCase.assertEquals("hello", arrayListBlock.get(0));
        TestCase.assertEquals("world", arrayListBlock.get(1));
    }

}