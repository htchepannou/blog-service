package com.tchepannou.blog.dao.jdbc;

import com.tchepannou.blog.dao.LogEventDao;

import javax.sql.DataSource;

public class JdbcLogEventDao implements LogEventDao {
    private DataSource ds;

    public JdbcLogEventDao(DataSource ds) {
        this.ds = ds;
    }
}
