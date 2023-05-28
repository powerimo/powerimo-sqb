package org.powerimo.sqb.std;

import lombok.Data;
import org.powerimo.sqb.*;

import java.util.LinkedList;
import java.util.List;

@Data
public class StdSearchParams implements SearchParamsProvider, FromInfo {
    private List<Condition> conditionList = new LinkedList<>();
    private String orderBy;
    private Integer limit;
    private Integer offset;
    private String selectSql;

    @Override
    public List<Condition> getConditions() {
        return conditionList;
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    @Override
    public Integer getLimitOffset() {
        return offset;
    }

    @Override
    public String getSelectText() {
        return null;
    }

    @Override
    public String getFromText() {
        return null;
    }

    @Override
    public String getSelectFromText() {
        return selectSql;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Condition> conditionList = new LinkedList<>();
        private String tableName;
        private String alias;
        private String selectSql;
        private Integer limit;
        private Integer limitOffset;

        public StdSearchParams build() {
            StdSearchParams params = new StdSearchParams();
            params.conditionList = conditionList;

            // build sql
            if (selectSql != null)
                params.selectSql = selectSql;
            else if (tableName != null) {
                params.selectSql = "select * from "
                        + tableName
                        + (alias != null ? " " + alias : "");
            }

            params.limit = limit;
            params.offset = limitOffset;
            return params;
        }

        public Builder table(String tableName, String alias) {
            this.tableName = tableName;
            this.alias = tableName == null ? null : alias;
            return this;
        }

        public Builder table(String table) {
            return this.table(table, null);
        }

        public Builder alias(String value) {
            this.alias = value;
            return this;
        }

        public Builder selectSql(String sql) {
            this.selectSql = sql;
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

        public Builder limit(Integer limitValue, Integer offset) {
            this.limit = limitValue;
            this.limitOffset = offset;
            return this;
        }

        public Builder limit(Integer limitValue) {
            return limit(limitValue, null);
        }
    }
}
