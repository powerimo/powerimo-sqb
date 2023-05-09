package org.powerimo.sqb.std;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMapSearchParams {

    @Test
    void builderTest() {
        var params = MapSearchParams.builder()
                .table("table1")
                .alias("t")
                .whereEqual("f1", "aaa")
                .whereNullableEqual("f2", "bbb")
                .whereNullableEqual("f3", null)
                .whereInSql("f4", "select id from t2")
                .whereNotInSql("f4", "select id from t3")
                .build();

        assertEquals(4, params.getConditionList().size());
        assertEquals("table1", params.getPrimaryTable().getTable());
        assertEquals("t", params.getPrimaryTable().getAlias());
    }

}
