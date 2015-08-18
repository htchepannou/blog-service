package com.tchepannou.blog.config;

import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.dao.jdbc.JdbcPostDao;
import com.tchepannou.blog.dao.jdbc.JdbcPostEntryDao;
import com.tchepannou.blog.dao.jdbc.JdbcPostTagDao;
import com.tchepannou.blog.dao.jdbc.JdbcTagDao;
import com.tchepannou.blog.service.AccessTokenService;
import com.tchepannou.blog.service.CreateTextCommand;
import com.tchepannou.blog.service.GetPostListCommand;
import com.tchepannou.blog.service.GetPostCommand;
import com.tchepannou.blog.service.HttpClientProvider;
import com.tchepannou.blog.service.impl.AccessTokenServiceImpl;
import com.tchepannou.blog.service.impl.CreateTextCommandImpl;
import com.tchepannou.blog.service.impl.GetPostListCommandImpl;
import com.tchepannou.blog.service.impl.GetPostCommandImpl;
import com.tchepannou.blog.service.impl.HttpClientProviderImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class AppConfig {
    @Value("${database.driver}")
    private String driver;

    @Value ("${database.url}")
    private String url;

    @Value ("${database.username}")
    private String username;

    @Value ("${database.password}")
    private String password;


    //-- Beans
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    DataSource dataSource (){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        return ds;
    }

    @Bean
    HttpClientProvider httpClientProvider(){
        return new HttpClientProviderImpl();
    }

    @Bean
    AccessTokenService accessTokenService(){
        return new AccessTokenServiceImpl();
    }

    @Bean
    PostDao postDao (){
        return new JdbcPostDao(dataSource());
    }

    @Bean
    PostTagDao postTagDao () {
        return new JdbcPostTagDao (dataSource());
    }

    @Bean
    PostEntryDao postEntryDao() {
        return new JdbcPostEntryDao(dataSource());
    }

    @Bean
    TagDao tagDao (){
        return new JdbcTagDao(dataSource());
    }

    @Bean
    GetPostCommand getPostCommand(){
        return new GetPostCommandImpl();
    }

    @Bean
    GetPostListCommand getPostListCommand(){
        return new GetPostListCommandImpl();
    }

    @Bean
    CreateTextCommand createTextCommand () {
        return new CreateTextCommandImpl();
    }
}
