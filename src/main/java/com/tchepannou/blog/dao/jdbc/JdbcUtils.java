package com.tchepannou.blog.dao.jdbc;

import java.util.Collection;

public class JdbcUtils {
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
}
