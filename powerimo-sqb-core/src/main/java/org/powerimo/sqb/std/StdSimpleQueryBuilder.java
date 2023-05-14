package org.powerimo.sqb.std;

import lombok.NonNull;
import org.powerimo.sqb.*;

import java.util.LinkedList;
import java.util.List;

public class StdSimpleQueryBuilder implements SimpleQueryBuilder, QueryDetails {
    private final List<QueryDetailParam> queryDetailParams = new LinkedList<>();
    private String selectPart;
    private String fromPart;
    private String selectFromPart;
    private StringBuilder wherePart = new StringBuilder();
    private String orderPart;
    private String limitPart;
    private int paramCounter = 0;
    private String queryText;
    private ConditionResolverConfig config;
    private FromInfo fromInfo;
    private SearchParamsProvider searchParamsProvider;

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
        this.searchParamsProvider = searchParamsProvider;
        prepare();
    }

    public void prepare(@NonNull FromInfo fromInfo, @NonNull SearchParamsProvider searchParamsProvider) {
        this.fromInfo = fromInfo;
        this.searchParamsProvider = searchParamsProvider;
        prepare();
    }

    public void prepare() {
        if (fromInfo == null)
            throw new SqbException("The field fromInformationProvider must not be null");
        if (searchParamsProvider == null)
            throw new SqbException("The field searchParamsProvider must not be null");
        if (this.config == null) {
            this.config = new StdConditionResolverConfig();
        }

        selectPart = null;
        selectFromPart = null;
        fromPart = null;
        queryText = null;
        queryDetailParams.clear();
        paramCounter = 0;

        parseSelectInfo();
        parseConditions();

        StringBuilder result = new StringBuilder();
        result.append(selectFromPart);
        if (wherePart != null)
            result.append(" ").append(wherePart);
        if (orderPart != null)
            result.append(" ").append(orderPart);
        if (limitPart != null)
            result.append(" ").append(limitPart);

        queryText = result.toString();
    }

    protected void parseSelectInfo() {
        selectFromPart = fromInfo.getSelectFromText();
        if (selectFromPart == null) {
            selectPart = fromInfo.getSelectText();
            fromPart = fromInfo.getFromText();
            selectFromPart = selectPart + " " + fromPart;
        }
    }

    protected void parseConditions() {
        for (var item: searchParamsProvider.getConditions()) {
            var resolver = config.getResolver(item);
            if (resolver != null) {
                resolver.handleCondition(item, this);
            }
        }

        if (searchParamsProvider.getLimit() != null) {
            if (searchParamsProvider.getLimitOffset() != null) {
                limitPart = "limit " +
                        searchParamsProvider.getLimit() +
                        " offset " +
                        searchParamsProvider.getLimitOffset();
            } else {
                limitPart = "limit " + searchParamsProvider.getLimit().toString();
            }
        }
    }

    @Override
    public String getQueryText() {
        return queryText;
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
    public void setLimitPart(String value) {
        limitPart = value;
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

    @Override
    public void prepare(SearchParamsProvider searchParamsProvider, FromInfo fromInfo) {
        this.searchParamsProvider = searchParamsProvider;
        this.fromInfo = fromInfo;
        prepare();
    }

    @Override
    public StdSimpleQueryBuilder searchParams(SearchParamsProvider provider) {
        this.searchParamsProvider = provider;
        return this;
    }

    @Override
    public StdSimpleQueryBuilder fromProvider(FromInfo provider) {
        this.fromInfo = provider;
        return this;
    }
}
