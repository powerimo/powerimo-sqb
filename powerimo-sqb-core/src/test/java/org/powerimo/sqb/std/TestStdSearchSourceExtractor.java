package org.powerimo.sqb.std;

import org.junit.jupiter.api.Test;
import org.powerimo.sqb.Condition;
import org.powerimo.sqb.ConditionResolver;
import org.powerimo.sqb.ConditionType;
import org.powerimo.sqb.QueryDetails;
import org.powerimo.sqb.annotations.Limit;
import org.powerimo.sqb.annotations.SearchParam;
import org.powerimo.sqb.annotations.SearchSource;

import static org.junit.jupiter.api.Assertions.*;

public class TestStdSearchSourceExtractor {

    @SearchSource(table = "table1", alias ="t")
    public static class ExampleSearchParams {
        @SearchParam(fieldName = "f1")
        String field1 = "aaa";
        @SearchParam(fieldName = "f2")
        Integer field2 = 123;
        @SearchParam(fieldName = "f3", conditionType = ConditionType.CUSTOM)
        MyClass1 myClass1 = new MyClass1("a1", "b1");
        @Limit
        Integer limit = 10;
    }

    public static class MyClass1 {
        final String customField1;
        final String customField2;

        MyClass1(String f1, String f2) {
            customField1 = f1;
            customField2 = f2;
        }
    }

    public static class MyCustomResolver implements ConditionResolver {
        @Override
        public void handleCondition(Condition condition, QueryDetails details) {
            details.setLimitPart("limit 0");
        }
    }

    @Test
    void testCreation() {
        var searchParams = new ExampleSearchParams();
        var extractor = new StdSearchSourceExtractor(searchParams);

        assertNotNull(extractor);
        assertTrue(extractor.isSourceObject());
        assertEquals(3, extractor.getConditions().size());
        assertEquals(10, extractor.getLimit());
    }

}
