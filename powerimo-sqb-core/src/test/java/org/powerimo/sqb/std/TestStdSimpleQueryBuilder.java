package org.powerimo.sqb.std;

import org.junit.jupiter.api.Test;
import org.powerimo.sqb.ConditionType;
import org.powerimo.sqb.annotations.Limit;
import org.powerimo.sqb.annotations.OrderBy;
import org.powerimo.sqb.annotations.SearchParam;
import org.powerimo.sqb.annotations.SearchSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestStdSimpleQueryBuilder {

    @SearchSource(table = "table1", alias ="t")
    public static class ExampleSearchParamsOrdered2 {
        @SearchParam(fieldName = "a.f1")
        String field1 = "aaa";
        @SearchParam(fieldName = "a.f2")
        Integer field2 = 123;
        @SearchParam(fieldName = "a.f3", conditionType = ConditionType.CUSTOM)
        TestStdSearchSourceExtractor.MyClass1 myClass1 = new TestStdSearchSourceExtractor.MyClass1("a1", "b1");
        @Limit
        Integer limit = 10;
        @OrderBy
        String orderString = "field1 asc, field2 desc, myClass1 asa, f4, f5";
    }

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

    @Test
    void testWithExtractor() {
        var params = new ExampleSearchParamsOrdered2();
        var qb = new StdSimpleQueryBuilder()
                .fromProvider(new FromStringInfo("select *", "from table1 t"));
        var text = qb.prepareText(new StdSearchSourceExtractor(params));

        assertNotNull(text);
        assertEquals("select * from table1 t where a.f1=:p1 and a.f2=:p2 and a.f3 order by a.f1 ASC,a.f2 DESC,a.f3 ASC limit 10", text);
    }

}
