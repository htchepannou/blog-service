package com.tchepannou.blog.dao.jdbc;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

public class JdbcUtils {
    private JdbcUtils (){

    }

    public static String toParamVars(Collection items){
        StringBuilder params = new StringBuilder();
        items.stream().forEach(post -> {
            if (params.length()>0){
                params.append(',');
            }
            params.append('?');
        });
        return params.toString();
    }

    public static Timestamp toTimestamp (Date date){
        return date != null ? new Timestamp(date.getTime()) : null;
    }
}
