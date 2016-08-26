package com.maxent.dscache.cache;

/**
 * Created by alain on 16/8/20.
 */
public class TestCacheEntry implements ICacheEntry {
    private String field1;
    private String field2;

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    @Override
    public String key() {
        return "";
    }

    @Override
    public double match(ICacheEntry entry) {
        return 1;
    }

    @Override
    public double threadshold() {
        return 0.5;
    }
}
