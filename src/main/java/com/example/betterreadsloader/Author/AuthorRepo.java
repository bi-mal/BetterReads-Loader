package com.example.betterreadsloader.Author;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface AuthorRepo extends CassandraRepository<Author,String> {

}
