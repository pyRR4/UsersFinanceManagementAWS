package com.example.repository;

import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;

@RequiredArgsConstructor
public abstract class AbstractJdbcRepository {

    protected final DataSource dataSource;
}
