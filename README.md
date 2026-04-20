# Powerimo Simple Query Builder (SQB)

A library for dynamic construction of SQL queries based on search parameters. It allows for easy transformation of objects (e.g., DTOs from REST requests) into SQL queries using annotations or a programmatic interface.

## Modules

*   **powerimo-sqb-core**: Core logic for building queries. It does not depend on external frameworks.
*   **powerimo-sqb-starter**: Spring Boot Starter for integration with Spring JDBC and JPA.

## Key Features

*   Building `SELECT` queries with automatic generation of `WHERE` conditions.
*   Support for various types of conditions: `EQUAL`, `LIKE`, `IN`, `GREATER`, `LESS`, etc.
*   Use of annotations to describe data sources and search parameters.
*   Support for Named Parameters.
*   Integration with JPA `EntityManager` for executing native queries.

## Usage (Core)

### Describing Search Parameters via Annotations

```java
@SearchSource(table = "my_table", alias = "t")
public class MySearchParams {
    @SearchParam(fieldName = "t.name")
    private String name;
    
    @SearchParam(fieldName = "t.status", conditionType = ConditionType.IN_LIST)
    private List<String> statuses;
}
```

### Generating a Query

```java
MySearchParams params = new MySearchParams();
params.setName("John");

ProvidersConfig info = SearchAnnotationProcessor.extractInfo(params);
StdSimpleQueryBuilder builder = new StdSimpleQueryBuilder();
builder.prepare(info.getFromInfo(), info.getSearchParamsProvider());

String sql = builder.getQueryText(); // select * from my_table t where t.name = :p1
List<QueryDetailParam> queryParams = builder.getQueryParams();
```

## Usage (Spring Boot Starter)

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.powerimo</groupId>
    <artifactId>powerimo-sqb-starter</artifactId>
    <version>${powerimo-sqb.version}</version>
</dependency>
```

### Example with JPA

```java
public List<MyEntity> search(MySearchParams params) {
    Sqb sqb = new Sqb(params);
    return sqb.executeJpaQuery(entityManager, MyEntity.class);
}
```

### Example with NamedParameterJdbcTemplate

```java
public List<MyEntity> search(MySearchParams params) {
    Sqb sqb = new Sqb(params);
    return jdbcTemplate.query(sqb.getQuery(), sqb.getNamedParams(), rowMapper);
}
```

## @SearchSource Annotation Configuration

*   `table`: Table name.
*   `alias`: Table alias.
*   `resource`: Path to an SQL file in resources (for complex queries).
*   `selectFromSql`: Direct specification of the `SELECT ... FROM ...` part.

## Supported Conditions (ConditionType)

*   `EQUAL`, `NOT_EQUAL`
*   `LIKE`
*   `IS_NULL`, `IS_NOT_NULL`
*   `GREATER`, `GREATER_OR_EQUAL`
*   `LESS`, `LESS_OR_EQUAL`
*   `IN_LIST`, `NOT_IN_LIST`
*   `IN_SQL`, `NOT_IN_SQL`
*   `CUSTOM`
