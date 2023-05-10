package org.powerimo.sqb.starter;

import org.junit.jupiter.api.Test;
import org.powerimo.sqb.ConditionType;
import org.powerimo.sqb.annotations.Limit;
import org.powerimo.sqb.annotations.SearchParam;
import org.powerimo.sqb.annotations.SearchSource;

import static org.junit.jupiter.api.Assertions.*;

public class TestSqb {

    @SuppressWarnings("unused")
    @SearchSource(table = "table1", alias = "t1")
    public static class SampleParams1 {
        @SearchParam(fieldName = "f1")
        String field1 = "aaa";
        @SearchParam(conditionType = ConditionType.GREATER)
        Integer field2 = 123;
        @Limit
        Integer limitField = 9;
    }

    @Test
    void searchSourceTest() {
        var params = new SampleParams1();
        Sqb sqb = new Sqb(params);

        assertNotNull(sqb);
        assertNotNull(sqb.getNamedParams());
        assertNotNull(sqb.getQuery());
        assertEquals(2, sqb.getNamedParams().getParameterNames().length);
        assertTrue(sqb.getQuery().contains("f1"));
        assertTrue(sqb.getQuery().contains("field2"));
        assertNotNull(sqb.getDetailParams());
    }

}
