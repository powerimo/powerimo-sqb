package org.powerimo.sqb.std;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestStdSimpleQueryBuilder {

    @Test
    void standard1() {
        var params = StdSearchParams.builder()
                .whereEqual("f1", "aaa")
                .whereNullableEqual("f2", "bbb")
                .whereNullableEqual("f3", null)
                .whereInSql("f4", "(select id from t2)")
                .build();

        var qb = new StdSimpleQueryBuilder()
                .searchParams(params)
                .fromProvider(new FromStringInfo("select *", "from table1 t"));
        var text = qb.prepareText(params);

        assertNotNull(text);
        assertEquals(2, qb.getQueryParams().size());
        assertEquals("select * from table1 t where f1=:p1 and f2=:p2 and f4 in (select id from t2)", text);
    }
}
