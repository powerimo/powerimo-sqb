package org.powerimo.sqb.std;

import org.powerimo.sqb.FromInfo;

public class FromBuilderProvider implements FromInfo {
    private String selectText;
    private String fields;
    private String fromText;
    private String joinText;

    @Override
    public String getSelectText() {
        return selectText;
    }

    @Override
    public String getFromText() {
        if (joinText != null && !joinText.isEmpty()) {
            return fromText + " " + joinText;
        } else {
            return fromText;
        }
    }

    @Override
    public String getSelectFromText() {
        return getSelectText() + " " + getFromText();
    }

    public FromBuilderProvider fromTable(String tableName, String alias) {

        if (tableName != null) {
            fromText = "from " + tableName;
            if (alias != null)
                fromText = fromText + " " + alias;
        } else
            fromText = null;

        return this;
    }

    public FromBuilderProvider selectPart(String part) {
        this.selectText = part;
        return this;
    }

    public FromBuilderProvider fromPart(String part) {
        this.fromText = part;
        return this;
    }

    public FromBuilderProvider addJoinText(String text) {
        if (joinText == null)
            joinText = text;
        else
            joinText = joinText + " " + text;
        return this;
    }

    public FromBuilderProvider addField(String field) {
        if (fields != null)
            this.fields = fields + "," + field;
        else
            this.fields = field;
        updateSelect();
        return this;
    }

    public FromBuilderProvider fields(String fields) {
        this.fields = fields;
        updateSelect();
        return this;
    }

    protected void updateSelect() {
        selectText = "select " + fields;
    }

    public FromBuilderProvider join(String table, String alias, String onClause) {
        String txt = "join " + table
                + (alias != null ? " " + alias : "")
                + " " + onClause;
        return addJoinText(txt);
    }

    public JoinBuilder join() {
        return new JoinBuilder(this, null);
    }

    public JoinBuilder leftJoin() {
        return new JoinBuilder(this, "left");
    }

    public JoinBuilder rightJoin() {
        return new JoinBuilder(this, "left");
    }

    public static class JoinBuilder {
        private FromBuilderProvider provider;
        private String table;
        private String alias;
        private String onClause;
        private String joinType;

        public JoinBuilder(FromBuilderProvider provider, String joinType) {
            this.provider = provider;
            this.joinType = joinType;
        }

        public JoinBuilder table(String table, String alias) {
            this.table = table;
            this.alias = alias;
            return this;
        }

        public JoinBuilder on(String leftFieldOn, String rightTableAlias, String rightFieldOn) {
            this.onClause = (joinType != null ? joinType + " " : "")
                + "join "
                + table
                + (alias != null ? " " +  alias : "")
                + " on "
                + rightTableAlias + "." + rightFieldOn;
            return this;
        }

        public JoinBuilder on(String text) {
            this.onClause =
                    (joinType != null ? joinType + " " : "")
                            + table
                            + (alias != null ? " " +  alias : "")
                            + " on "
                    + text;
            return this;
        }

        public FromBuilderProvider ok() {
            provider.addJoinText(onClause);
            return provider;
        }
    }

}
