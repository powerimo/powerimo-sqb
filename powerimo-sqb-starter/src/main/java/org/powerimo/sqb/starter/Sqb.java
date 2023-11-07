package org.powerimo.sqb.starter;

import org.powerimo.sqb.FromInfo;
import org.powerimo.sqb.QueryDetailParam;
import org.powerimo.sqb.SearchParamsProvider;
import org.powerimo.sqb.SearchAnnotationProcessor;
import org.powerimo.sqb.std.StdSimpleQueryBuilder;
import org.powerimo.sqb.std.StdSearchSourceExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;

public class Sqb {
    private final StdSimpleQueryBuilder queryBuilder;
    private final Object params;
    private SearchParamsProvider searchParamsProvider;
    private FromInfo fromInfo;

    public Sqb(Object params, FromInfo fromProvider) {
        this.queryBuilder = new StdSimpleQueryBuilder();
        this.params = params;
        this.fromInfo = fromProvider;
        initSearchProvider();
        prepare();
    }

    public Sqb(Object params) {
        this.queryBuilder = new StdSimpleQueryBuilder();
        this.params = params;
        initSearchProvider();
        prepare();
    }

    protected void initSearchProvider() {
        if (params instanceof SearchParamsProvider) {
            searchParamsProvider = (SearchParamsProvider) params;
        } else {
            searchParamsProvider = new StdSearchSourceExtractor(params);
        }
    }

    protected void prepare() {
        var info = SearchAnnotationProcessor.extractInfo(params);
        if (fromInfo != null)
            info.setFromInfo(fromInfo);
        queryBuilder.prepare(info.getFromInfo(), info.getSearchParamsProvider());
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
