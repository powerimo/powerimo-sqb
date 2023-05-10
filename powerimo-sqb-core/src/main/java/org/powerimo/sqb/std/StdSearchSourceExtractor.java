package org.powerimo.sqb.std;

import lombok.NonNull;
import org.powerimo.sqb.Condition;
import org.powerimo.sqb.SearchParamsProvider;
import org.powerimo.sqb.TableSource;
import org.powerimo.sqb.annotations.Limit;
import org.powerimo.sqb.annotations.SearchParam;
import org.powerimo.sqb.annotations.SearchSource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StdSearchSourceExtractor implements SearchParamsProvider {
    private final TableSource primaryTable = new TableSource();
    private SearchSource searchSourceAnnotation;
    private Object searchSourceObject;
    private final List<Condition> conditionList = new LinkedList<>();
    private String selectFields = "*";
    private String orderBy;
    private Integer limit;
    private boolean isSourceObject = false;

    public StdSearchSourceExtractor(@NonNull Object obj) {
        isSourceObject = false;
        if (obj.getClass().isAnnotationPresent(SearchSource.class)) {
            var annotation = (obj.getClass().getAnnotation(SearchSource.class));
            primaryTable.setTable(annotation.table());
            primaryTable.setAlias(annotation.alias());
            searchSourceAnnotation = annotation;
            searchSourceObject = obj;
            isSourceObject = true;
            readAttributes();
        }
    }

    protected void readAttributes() {
        var fields = getFieldsWithAnnotation(searchSourceObject.getClass(), SearchParam.class);
        fields.forEach( (field) -> {
            var searchParam = field.getAnnotation(SearchParam.class);
            addCondition(searchParam, field);
        });

        fields = getFieldsWithAnnotation(searchSourceObject.getClass(), Limit.class);
        if (fields.size() > 0) {
            var limitParam = fields.get(0).getAnnotation(Limit.class);
            populateLimit(limitParam, fields.get(0));
        }

    }

    protected void addCondition(SearchParam searchParam, Field field) {
        field.setAccessible(true);

        Object value;
        try {
            value =  field.get(searchSourceObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        String searchFieldName;
        if (searchParam.fieldName() != null && !searchParam.fieldName().isEmpty()) {
            searchFieldName = searchParam.fieldName();
        } else {
            searchFieldName = field.getName();
        }

        Condition condition = Condition.builder()
                .field(searchFieldName)
                .value(value)
                .type(searchParam.conditionType())
                .sqlType(searchParam.sqlType())
                .sqlTypeName(searchParam.sqlTypeName())
                .resolverClass(searchParam.resolver())
                .customConditionText(searchParam.customConditionText())
                .build();
        conditionList.add(condition);
    }

    protected void populateLimit(Limit limitParam, Field field) {
        field.setAccessible(true);
        Object value;
        try {
            value =  field.get(searchSourceObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (value == null) {
            limit = null;
            return;
        }

        if (value instanceof Integer) {
            limit = (Integer) value;
        } else if (value instanceof Long) {
            limit = ((Long) value).intValue();
        } else
            limit = null;
    }

    public static List<Field> getFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Field> annotatedFields = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotation)) {
                annotatedFields.add(field);
            }
        }

        return annotatedFields;
    }

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
        primaryTable(source.getTable(), source.getAlias());
    }

    @Override
    public void primaryTable(String table, String alias) {
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
    public String getOrderBy() {
        return orderBy;
    }

    @Override
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    @Override
    public void setLimit(Integer value) {
        limit = value;
    }

    public boolean isSourceObject() {
        return this.isSourceObject;
    }
}
