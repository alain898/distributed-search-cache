package com.alain898.dscache.cache;

import org.apache.commons.lang3.StringUtils;

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
        return StringUtils.join(field1, field2);
    }

    @Override
    public double match(ICacheEntry entry) {
        if (!(entry instanceof TestCacheEntry)) {
            return 0;
        }
        TestCacheEntry cEntry = (TestCacheEntry) entry;
        if (StringUtils.equals(field1, cEntry.field1) && StringUtils.equals(field2, cEntry.field2)) {
            return 1;
        } else if (StringUtils.equals(field1, cEntry.field1)) {
            return 0.8;
        } else if (StringUtils.equals(field2, cEntry.field2)) {
            return 0.5;
        } else {
            return 0;
        }
    }

    @Override
    public double threadshold() {
        return 0.5;
    }
}
