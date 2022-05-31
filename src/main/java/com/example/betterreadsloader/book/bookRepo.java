package com.example.betterreadsloader.book;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface bookRepo extends CassandraRepository<Book,String> {
}
