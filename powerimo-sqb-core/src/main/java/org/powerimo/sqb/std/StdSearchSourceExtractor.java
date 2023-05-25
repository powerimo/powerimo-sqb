package org.powerimo.sqb.std;

import lombok.NonNull;
import org.powerimo.sqb.Condition;
import org.powerimo.sqb.SearchParamsProvider;
import org.powerimo.sqb.annotations.Limit;
import org.powerimo.sqb.annotations.Offset;
import org.powerimo.sqb.annotations.SearchParam;
import org.powerimo.sqb.annotations.SearchSource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StdSearchSourceExtractor implements SearchParamsProvider {
    private SearchSource searchSourceAnnotation;
    private Object searchSourceObject;
    private final List<Condition> conditionList = new LinkedList<>();
    private String orderBy;
    private Integer limit;
    private Integer offset;
    private boolean isSourceObject = false;

    public StdSearchSourceExtractor(@NonNull Object obj) {
        isSourceObject = false;
        if (obj.getClass().isAnnotationPresent(SearchSource.class)) {
            var annotation = (obj.getClass().getAnnotation(SearchSource.class));
            searchSourceAnnotation = annotation;
            searchSourceObject = obj;
            isSourceObject = true;
            readAttributes();
        }
    }

    protected void readAttributes() {
        // extract conditions
        var fields = getFieldsWithAnnotation(searchSourceObject.getClass(), SearchParam.class);
        fields.forEach( (field) -> {
            var searchParam = field.getAnnotation(SearchParam.class);
            addCondition(searchParam, field);
        });

        // extract limit
        fields = getFieldsWithAnnotation(searchSourceObject.getClass(), Limit.class);
        if (fields.size() > 0) {
            var limitParam = fields.get(0).getAnnotation(Limit.class);
            populateLimit(limitParam, fields.get(0));
        }

        // extract offset
        fields = getFieldsWithAnnotation(searchSourceObject.getClass(), Offset.class);
        if (fields.size() > 0) {
            var limitParam = fields.get(0).getAnnotation(Offset.class);
            populateLimitOffset(limitParam, fields.get(0));
        }
    }

    /**
     * add a condition base on an annotation and a field
     * @param searchParam an annotation object
     * @param field a field of source object class
     */
    protected void addCondition(SearchParam searchParam, Field field) {
        field.setAccessible(true);

        Object value;
        try {
            value =  field.get(searchSourceObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // if the fieldName value in annotation is specified, it will be used
        // if not, the field name in object class will beused
        String searchFieldName;
        if (searchParam.fieldName() != null && !searchParam.fieldName().isEmpty()) {
            searchFieldName = searchParam.fieldName();
        } else {
            searchFieldName = field.getName();
        }

        // check null value with the ignoreNullsFlag
        if (value == null && searchParam.ignoreNulls()) {
            return;
        }

        // add a condition to the list of conditions
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

    /**
     * Set limit based on the Limit annotation field
     * @param limitParam an annotation object
     * @param field a field
     */
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

    /**
     * Set offset in limit based on Offset annotation
     * @param offsetParam an annotation object
     * @param field a field of source object
     */
    protected void populateLimitOffset(Offset offsetParam, Field field) {
        field.setAccessible(true);
        Object value;
        try {
            value =  field.get(searchSourceObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (value == null) {
            offset = null;
            return;
        }

        if (value instanceof Integer) {
            offset = (Integer) value;
        } else if (value instanceof Long) {
            offset = ((Long) value).intValue();
        } else
            offset = null;
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
    public String getOrderBy() {
        return orderBy;
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    public boolean isSourceObject() {
        return this.isSourceObject;
    }

    @Override
    public Integer getLimitOffset() {
        return offset;
    }
}
