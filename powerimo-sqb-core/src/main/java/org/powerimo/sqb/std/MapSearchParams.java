package org.powerimo.sqb.std;

import lombok.Data;
import org.powerimo.sqb.Condition;
import org.powerimo.sqb.ConditionType;
import org.powerimo.sqb.SearchParamsProvider;
import org.powerimo.sqb.TableSource;

import java.util.LinkedList;
import java.util.List;

@Data
public class MapSearchParams implements SearchParamsProvider {
    private List<Condition> conditionList = new LinkedList<>();
    private TableSource primaryTable;
    private String selectFields = "*";
    private String orderBy;
    private Integer limit;

    @Override
    public List<Condition> getConditions() {
        return conditionList;
    }

    @Override
    public TableSource getPrimaryTable() {
        return primaryTable;
    }

    @Override
    public void setPrimaryTable(TableSource source) {
        primaryTable = source;
    }

    @Override
    public void primaryTable(String table, String alias) {
        primaryTable = new TableSource();
        primaryTable.setTable(table);
        primaryTable.setAlias(alias);
    }

    @Override
    public String getSelectFields() {
        return selectFields;
    }

    @Override
    public void setSelectFields(String fields) {
        selectFields = fields;
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    @Override
    public void setLimit(Integer value) {
        this.limit = value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Condition> conditionList = new LinkedList<>();
        private String table;
        private String alias;

        public MapSearchParams build() {
            MapSearchParams params = new MapSearchParams();
            params.conditionList = conditionList;
            params.primaryTable(table, alias);
            return params;
        }

        public Builder table(String table) {
            this.table = table;
            return this;
        }

        public Builder alias(String alias) {
            this.alias = alias;
            return this;
        }

        public Builder whereEqual(String field, Object value) {
            Condition condition = new Condition();
            condition.setField(field);
            condition.setType(ConditionType.EQUAL);
            conditionList.add(condition);
            return this;
        }

        public Builder whereNullableEqual(String field, Object value) {
            if (value == null)
                return this;
            return whereEqual(field, value);
        }

        public Builder whereNull(String field) {
            Condition condition = new Condition();
            condition.setField(field);
            condition.setType(ConditionType.IS_NULL);
            conditionList.add(condition);
            return this;
        }

        public Builder whereNotNull(String field) {
            Condition condition = new Condition();
            condition.setField(field);
            condition.setType(ConditionType.IS_NOT_NULL);
            conditionList.add(condition);
            return this;
        }

        public Builder whereInSql(String field, String sql) {
            Condition condition = new Condition();
            condition.setField(field);
            condition.setType(ConditionType.IN_SQL);
            condition.setCustomConditionText(sql);
            conditionList.add(condition);
            return this;
        }

        public Builder whereNotInSql(String field, String sql) {
            Condition condition = new Condition();
            condition.setField(field);
            condition.setType(ConditionType.NOT_IN_SQL);
            condition.setCustomConditionText(sql);
            conditionList.add(condition);
            return this;
        }

    }
}
