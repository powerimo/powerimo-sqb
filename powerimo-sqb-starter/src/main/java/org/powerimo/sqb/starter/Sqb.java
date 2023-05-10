package org.powerimo.sqb.starter;

import org.powerimo.sqb.QueryDetailParam;
import org.powerimo.sqb.SearchParamsProvider;
import org.powerimo.sqb.std.StandardSimpleQueryBuilder;
import org.powerimo.sqb.std.StdSearchSourceExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;

public class Sqb {
    private final StandardSimpleQueryBuilder queryBuilder;

    public Sqb(Object params) {
        queryBuilder = new StandardSimpleQueryBuilder();
        SearchParamsProvider searchParamsProvider;
        if (params instanceof SearchParamsProvider) {
            searchParamsProvider = (SearchParamsProvider) params;
        } else {
            searchParamsProvider = new StdSearchSourceExtractor(params);
        }
        queryBuilder.prepare(searchParamsProvider);
    }

    public String getQuery() {
        return queryBuilder.getQueryText();
    }

    public MapSqlParameterSource getNamedParams() {
        return SqbTool.createNamedParams(queryBuilder);
    }

    public List<QueryDetailParam> getDetailParams() {
        return queryBuilder.getQueryParams();
    }

}
