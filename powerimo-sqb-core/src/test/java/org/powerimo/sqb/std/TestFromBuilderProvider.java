package org.powerimo.sqb.std;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestFromBuilderProvider {

    @Test
    void testProviderCreation() {
        var provider = new FromBuilderProvider()
                .fromTable("table1", "t1")
                .join()
                .table("table2", "t2")
                .on("parent_id", "t1", "id")
                .ok()
                .addJoinText("left join table3 t3 on t3.parent_id=t1.id")
                .leftJoin()
                .table("table4", "t4").on("t4.parent_id=t1.id")
                .ok();

        assertNotNull(provider.getFromText());
    }

    @Test
    void testManual() {
        var provider = new FromBuilderProvider()
                .fromTable("table1", "t1")
                .fields("t1.*");
        assertNotNull(provider.getFromText());
        assertEquals("select t1.*", provider.getSelectText());
        assertEquals("from table1 t1", provider.getFromText());
    }
}
