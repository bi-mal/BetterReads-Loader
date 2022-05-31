package com.example.betterreadsloader.book;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDate;
import java.util.List;

@Table("book_by_id")
public class Book {
    @Id
    @PrimaryKeyColumn(name="book_id",ordinal = 0,type= PrimaryKeyType.PARTITIONED)
    private String id;

    @Column("book_name")
    @CassandraType(type= CassandraType.Name.TEXT)
    private String name;

    @Column("description")
    @CassandraType(type= CassandraType.Name.TEXT)
    private String description;

    @Column("published_date")
    @CassandraType(type= CassandraType.Name.DATE)
    private LocalDate published_date;

    @Column("cover_Ids")
    @CassandraType(type= CassandraType.Name.LIST,typeArguments=CassandraType.Name.TEXT)
    private List<String> coverIds;

    @Column("author_names")
    @CassandraType(type= CassandraType.Name.LIST,typeArguments=CassandraType.Name.TEXT)
    private List<String> authorNames;

    @Column("author_Ids")
    @CassandraType(type= CassandraType.Name.LIST,typeArguments=CassandraType.Name.TEXT)
    private List<String> authorIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getPublished_date() {
        return published_date;
    }

    public void setPublished_date(LocalDate published_date) {
        this.published_date = published_date;
    }

    public List<String> getCoverIds() {
        return coverIds;
    }

    public void setCoverIds(List<String> coverIds) {
        this.coverIds = coverIds;
    }

    public List<String> getAuthorNames() {
        return authorNames;
    }

    public void setAuthorNames(List<String> authorNames) {
        this.authorNames = authorNames;
    }

    public List<String> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<String> authorIds) {
        this.authorIds = authorIds;
    }
}
