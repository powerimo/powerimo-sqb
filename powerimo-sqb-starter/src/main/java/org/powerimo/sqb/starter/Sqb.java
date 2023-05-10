package org.powerimo.sqb.starter;

import org.powerimo.sqb.SearchParamsProvider;
import org.powerimo.sqb.std.StandardSimpleQueryBuilder;
import org.powerimo.sqb.std.StdSearchSourceExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class Sqb {
    private StandardSimpleQueryBuilder queryBuilder;
    private SearchParamsProvider searchParamsProvider;

    public Sqb(Object params) {
        queryBuilder = new StandardSimpleQueryBuilder();
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

}
