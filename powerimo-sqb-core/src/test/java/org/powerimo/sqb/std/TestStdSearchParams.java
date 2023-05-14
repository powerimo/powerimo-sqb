package org.powerimo.sqb.std;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestStdSearchParams {

    @Test
    void tableTest() {
        var params = StdSearchParams.builder()
                .table("table1")
                .alias("t")
                .whereEqual("f1", "aaa")
                .whereNullableEqual("f2", "bbb")
                .whereNullableEqual("f3", null)
                .whereInSql("f4", "select id from t2")
                .whereNotInSql("f4", "select id from t3")
                .limit(11, 151)
                .build();

        assertEquals(4, params.getConditionList().size());
        assertEquals("select * from table1 t", params.getSelectSql());
        assertEquals(11, params.getLimit());
        assertEquals(151, params.getOffset());
    }

    @Test
    void sqlTest() {
        var params = StdSearchParams.builder()
                .selectSql("select * from table t1")
                .whereEqual("t1.f1", "aaa")
                .build();

        assertEquals(1, params.getConditionList().size());
        assertEquals("select * from table t1", params.getSelectSql());
        assertNull(params.getLimit());
        assertNull(params.getOffset());
        assertNull(params.getOrderBy());
    }

}
