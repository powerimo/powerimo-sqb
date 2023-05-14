package org.powerimo.sqb.std;

import org.powerimo.sqb.FromInfo;

public class FromStringInfo implements FromInfo {
    private String selectPart;
    private String fromPart;
    private String selectFrom;

    @Override
    public String getSelectText() {
        return selectPart;
    }

    @Override
    public String getFromText() {
        return fromPart;
    }

    @Override
    public String getSelectFromText() {
        return selectFrom;
    }

    public void buildSelectFrom() {
        selectFrom = selectPart + " " + fromPart;
    }

    public FromStringInfo() {

    }

    public FromStringInfo(String select, String from) {
        this.selectPart = select;
        this.fromPart = from;
        buildSelectFrom();
    }

    public FromStringInfo select(String text) {
        this.selectPart = text;
        buildSelectFrom();
        return this;
    }

    public FromStringInfo from(String text) {
        this.fromPart = text;
        buildSelectFrom();
        return this;
    }

    public FromStringInfo selectFrom(String text) {
        selectFrom = text;
        return this;
    }

}
