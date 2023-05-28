package org.powerimo.sqb.std;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.powerimo.sqb.Condition;
import org.powerimo.sqb.OrderDirection;
import org.powerimo.sqb.SearchParamsProvider;
import org.powerimo.sqb.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

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
        var searchParamFields = getFieldsWithAnnotation(searchSourceObject.getClass(), SearchParam.class);
        searchParamFields.forEach( (field) -> {
            var searchParam = field.getAnnotation(SearchParam.class);
            addCondition(searchParam, field);
        });

        // extract limit
        var fields = getFieldsWithAnnotation(searchSourceObject.getClass(), Limit.class);
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

        // extract orderBy fields
        fields = getFieldsWithAnnotation(searchSourceObject.getClass(), OrderBy.class);
        if (fields.size() > 0) {
            var orderBy = fields.get(0).getAnnotation(OrderBy.class);
            populateOrderBy(orderBy, fields.get(0), searchParamFields);
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

    protected void populateOrderBy(OrderBy annotation, Field field, List<Field> searchParamFields) {
        field.setAccessible(true);
        Object value;
        try {
            value = field.get(searchSourceObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (value == null) {
            if (Objects.equals(annotation.defaultValue(), "")) {
                orderBy = "";
                return;
            }
            value = annotation.defaultValue();
        }

        String s = value.toString();
        var strings = Arrays.stream(s.split(",")).collect(Collectors.toList());
        var orderItems = convertOrderList(strings);

        if (annotation.ignoreUnknownFields()) {
            // build map of fields
            HashMap<String, Field> fieldsMap = new HashMap<>();
            searchParamFields.forEach((item) -> fieldsMap.put(item.getName().toLowerCase(), item));

            // iterator over order fields
            orderItems.forEach((orderItem -> {
                // get field from SearchParam fields list
                var f = fieldsMap.get(orderItem.itemName.toLowerCase());
                if (f != null) {
                    orderItem.knownField = true;
                    // get annotation SearchParam
                    var fieldAnnotation = f.getAnnotation(SearchParam.class);
                    // get field alias from annotation
                    orderItem.searchParamFieldName = fieldAnnotation.fieldName();
                }
            }));
        }

        // populate orderBy part
        final StringBuilder sb = new StringBuilder("order by ");
        if (annotation.ignoreUnknownFields()) {
            // build list with known fields (has related fields with @SearchParam annotation and same name
            var known = orderItems.stream()
                    .filter(orderItem -> orderItem.knownField)
                    .collect(Collectors.toList());

            // if there is no known field then exit
            if (known.isEmpty()) {
                orderBy = "";
                return;
            }

            var first = known.get(0);
            known.forEach(item -> {
                // if the element is the first element, no comma is used
                if (item != first) {
                    sb.append(",");
                }
                sb.append(item.getSearchParamFieldName())
                        .append(" ")
                        .append(item.getDirection().name());
            });
        } else {
            var first = orderItems.get(0);
            orderItems.forEach(orderItem -> {
                // if the element is the first element, no comma is used
                if (orderItem != first) {
                    sb.append(",");
                }
                sb.append(orderItem.getItemName())
                        .append(" ")
                        .append(orderItem.getDirection().name());
            });
        }

        orderBy = sb.toString();
    }

    public static List<OrderItem> convertOrderList(List<String> sourceList) {
        List<OrderItem> orderItems = new ArrayList<>();
        sourceList.forEach((item) -> orderItems.add(new OrderItem(item.trim())));
        return orderItems;
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

    @Getter
    @Setter
    public static class OrderItem {
        private String itemString;
        private String itemName;
        private String searchParamFieldName;
        private OrderDirection direction;
        private boolean knownField;

        public OrderItem(String itemString) {
            this.itemString = itemString;
            if (itemString.contains(" ")) {
                int spaceIndex = itemString.indexOf(" ");
                itemName = itemString.substring(0, spaceIndex);
                var directionString = itemString.substring(spaceIndex + 1);
                if (directionString.equalsIgnoreCase("desc"))
                    direction = OrderDirection.DESC;
                else
                    direction = OrderDirection.ASC;
            } else {
                itemName = itemString;
                direction = OrderDirection.ASC;
            }
        }

    }
}
