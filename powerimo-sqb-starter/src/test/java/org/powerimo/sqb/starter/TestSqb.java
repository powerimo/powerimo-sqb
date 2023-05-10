package org.powerimo.sqb.starter;

import org.junit.jupiter.api.Test;
import org.powerimo.sqb.annotations.SearchParam;
import org.powerimo.sqb.annotations.SearchSource;

import static org.junit.jupiter.api.Assertions.*;

public class TestSqb {

    @SearchSource(table = "table1", alias = "t1")
    public static class SampleParams1 {
        @SearchParam(fieldName = "f1")
        String field1 = "aaa";
        @SearchParam
        Integer field2 = 123;
    }

    @Test
    void baseTest() {
        var params = new SampleParams1();
        Sqb sqb = new Sqb(params);

        assertNotNull(sqb);
        assertNotNull(sqb.getNamedParams());
        assertNotNull(sqb.getQuery());
        assertEquals(2, sqb.getNamedParams().getParameterNames().length);
        assertTrue(sqb.getQuery().contains("f1"));
        assertTrue(sqb.getQuery().contains("field2"));
    }
}
