package com.tchepannou.blog.dao;

import com.tchepannou.blog.domain.EventLog;

import java.util.List;

@Deprecated
public interface EventLogDao {
    List<EventLog> findByPost (long postId, int limit, int offset);
    void create(EventLog event);
}
