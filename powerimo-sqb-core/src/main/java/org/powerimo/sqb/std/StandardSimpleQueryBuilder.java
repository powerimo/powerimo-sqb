package org.powerimo.sqb.std;

import lombok.NonNull;
import org.powerimo.sqb.*;
import org.powerimo.sqb.annotations.SearchSource;

import java.util.LinkedList;
import java.util.List;

public class StandardSimpleQueryBuilder implements SimpleQueryBuilder, QueryDetails {
    private final List<QueryDetailParam> queryDetailParams = new LinkedList<>();
    private String selectPart;
    private String fromPart;
    private StringBuilder wherePart = new StringBuilder();
    private String orderPart;
    private String limitPart;
    private int paramCounter = 0;
    private String queryText;
    private ConditionResolverConfig config;

    @Override
    public void config(ConditionResolverConfig config) {
        this.config = config;
    }

    @Override
    public String prepareText(SearchParamsProvider searchParamsProvider) {
        prepare(searchParamsProvider);
        return queryText;
    }

    @Override
    public void prepare(@NonNull SearchParamsProvider searchParamsProvider) {
        if (this.config == null) {
            this.config = new StdConditionResolverConfig();
        }

        queryText = null;
        queryDetailParams.clear();
        paramCounter = 0;

        selectPart = "select " + searchParamsProvider.getSelectFields();
        if (searchParamsProvider.getPrimaryTable() == null) {
            throw new SqbException("Build SQL failed. Primary table is null.");
        }
        if (searchParamsProvider.getPrimaryTable().getTable() == null) {
            throw new SqbException("Build SQL failed. Primary table is empty.");
        }
        fromPart = searchParamsProvider.getPrimaryTable().getTable();
        if (searchParamsProvider.getPrimaryTable().getAlias() != null) {
            fromPart = "from " + searchParamsProvider.getPrimaryTable().getTable()
                    + " " + searchParamsProvider.getPrimaryTable().getAlias();
        } else {
            fromPart = "from " + searchParamsProvider.getPrimaryTable().getTable();
        }

        for (var item: searchParamsProvider.getConditions()) {
            var resolver = config.getResolver(item);
            if (resolver != null) {
                resolver.handleCondition(item, this);
            }
        }

        if (searchParamsProvider.getLimit() != null) {
            limitPart = "limit " + searchParamsProvider.getLimit().toString();
        }

        StringBuilder result = new StringBuilder();
        result.append(selectPart).append(" ").append(fromPart);
        if (wherePart != null)
            result.append(" ").append(wherePart);
        if (orderPart != null)
            result.append(" ").append(orderPart);
        if (limitPart != null)
            result.append(" ").append(limitPart);

        queryText = result.toString();
    }

    @Override
    public String getQueryText() {
        return queryText;
    }

    protected String prepareFrom(SearchParamsProvider searchParamsProvider) {
        if (searchParamsProvider.getPrimaryTable() == null)
            throw new SqbException("Primary table is not specified");
        if (searchParamsProvider.getPrimaryTable().getTable() == null)
            throw new SqbException("Primary table is not specified");
        StringBuilder res = new StringBuilder();
        res.append("from ").append(searchParamsProvider.getPrimaryTable().getTable());
        if (searchParamsProvider.getPrimaryTable().getAlias() != null)
            res.append(" ").append(searchParamsProvider.getPrimaryTable().getAlias());
        return res.toString();
    }

    @Override
    public String getSelectPart() {
        return selectPart;
    }

    @Override
    public void setSelectPart(String value) {
        selectPart = value;
    }

    @Override
    public String getFromPart() {
        return fromPart;
    }

    @Override
    public void setFromPart(String value) {
        fromPart = value;
    }

    @Override
    public String getWherePart() {
        return wherePart.toString();
    }

    @Override
    public void setWherePart(String value) {
        wherePart = new StringBuilder(value);
    }

    @Override
    public String getLimitPart() {
        return limitPart;
    }

    @Override
    public void setLimitPart(String value) {
        limitPart = value;
    }

    @Override
    public String getOrderPart() {
        return orderPart;
    }

    @Override
    public void setOrderPart(String value) {
        orderPart = value;
    }

    @Override
    public List<QueryDetailParam> getQueryParams() {
        return queryDetailParams;
    }

    @Override
    public void addWhere(String value) {
        if (wherePart.toString().isEmpty()) {
            wherePart.append("where ").append(value);
        } else {
            wherePart.append(" and ").append(value);
        }
    }

    @Override
    public int getParamCounter() {
        paramCounter++;
        return paramCounter;
    }
}
