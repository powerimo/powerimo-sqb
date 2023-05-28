package org.powerimo.sqb.std;

import org.junit.jupiter.api.Test;
import org.powerimo.sqb.*;
import org.powerimo.sqb.annotations.Limit;
import org.powerimo.sqb.annotations.OrderBy;
import org.powerimo.sqb.annotations.SearchParam;
import org.powerimo.sqb.annotations.SearchSource;

import java.util.ArrayList;
import java.util.List;

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

    @SearchSource(table = "table1", alias ="t")
    public static class ExampleSearchParamsOrdered1 {
        @SearchParam(fieldName = "a.f1")
        String field1 = "aaa";
        @SearchParam(fieldName = "a.f2")
        Integer field2 = 123;
        @SearchParam(fieldName = "a.f3", conditionType = ConditionType.CUSTOM)
        MyClass1 myClass1 = new MyClass1("a1", "b1");
        @Limit
        Integer limit = 10;
        @OrderBy
        String orderString = "field1 asc, field2 desc, myClass1 asa, f4, f5";
    }

    @SearchSource(table = "table1", alias ="t")
    public static class ExampleSearchParamsOrdered2 {
        @SearchParam(fieldName = "a.f1")
        String field1 = "aaa";
        @SearchParam(fieldName = "a.f2")
        Integer field2 = 123;
        @SearchParam(fieldName = "a.f3", conditionType = ConditionType.CUSTOM)
        MyClass1 myClass1 = new MyClass1("a1", "b1");
        @Limit
        Integer limit = 10;
        @OrderBy(ignoreUnknownFields = false)
        String orderString = "field1 asc, field2 desc, myClass1 asa, f4, f5";
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

    @Test
    void testOrderByItem() {
        String s = "myName desc";
        var orderItem = new StdSearchSourceExtractor.OrderItem(s);
        assertEquals("myName", orderItem.getItemName());
        assertEquals(OrderDirection.DESC, orderItem.getDirection());
    }

    @Test
    void testOrderByItem2() {
        String s = "myName desc a"; // wrong string which has call default ordering direction
        var orderItem = new StdSearchSourceExtractor.OrderItem(s);
        assertEquals("myName", orderItem.getItemName());
        assertEquals(OrderDirection.ASC, orderItem.getDirection());
    }

    @Test
    void testOrderByItem3() {
        String s = "myName"; // name with default direction
        var orderItem = new StdSearchSourceExtractor.OrderItem(s);
        assertEquals("myName", orderItem.getItemName());
        assertEquals(OrderDirection.ASC, orderItem.getDirection());
    }

    @Test
    void testOrderList() {
        List<String> source = new ArrayList<>();
        source.add("field1");
        source.add("field2 desc");
        source.add("field3 desc AAA");
        var list = StdSearchSourceExtractor.convertOrderList(source);

        assertEquals(3, list.size());
        assertEquals(OrderDirection.ASC, list.get(0).getDirection());
        assertEquals(OrderDirection.DESC, list.get(1).getDirection());
        assertEquals(OrderDirection.ASC, list.get(2).getDirection());
    }

    @Test
    void testOrderExtractKnown() {
        var source = new ExampleSearchParamsOrdered1();
        var extractor = new StdSearchSourceExtractor(source);

        assertNotNull(extractor.getOrderBy());
        assertNotEquals("", extractor.getOrderBy());
    }

    @Test
    void testOrderExtractKnownEmpty() {
        var source = new ExampleSearchParamsOrdered1();
        source.orderString = "notExists1,notExists2";
        var extractor = new StdSearchSourceExtractor(source);

        assertNotNull(extractor.getOrderBy());
        assertEquals("", extractor.getOrderBy());
    }

    @Test
    void testOrderWithUnknownFields() {
        var source = new ExampleSearchParamsOrdered2();
        source.orderString = "notExists1,notExists2 deSC";
        var extractor = new StdSearchSourceExtractor(source);

        assertNotNull(extractor.getOrderBy());
        assertEquals("order by notExists1 ASC,notExists2 DESC", extractor.getOrderBy());
    }

    @Test
    void testOrderNull() {
        var source = new ExampleSearchParamsOrdered2();
        source.orderString = null;
        var extractor = new StdSearchSourceExtractor(source);

        assertNotNull(extractor.getOrderBy());
    }


}
