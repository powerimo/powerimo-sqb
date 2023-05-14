package org.powerimo.sqb.std;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFromStringInfo {

    @Test
    void testCreation() {
        var fromProvider = new FromStringInfo()
                .from("from table1 t1")
                .select("select t1.field1");
        Assertions.assertNotNull(fromProvider);
    }

    @Test
    void testCreation2() {
        String s1 = "from table1 t1";
        String s2 = "select t1.field1";
        var fromProvider = new FromStringInfo(s2, s1);
        Assertions.assertNotNull(fromProvider);
        Assertions.assertEquals(s1, fromProvider.getFromText());
        Assertions.assertEquals(s2, fromProvider.getSelectText());
    }
}
