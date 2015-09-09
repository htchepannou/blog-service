package com.tchepannou.blog.config;

import com.tchepannou.blog.dao.AttachmentDao;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.dao.jdbc.JdbcAttachmentDao;
import com.tchepannou.blog.dao.jdbc.JdbcPostDao;
import com.tchepannou.blog.dao.jdbc.JdbcPostEntryDao;
import com.tchepannou.blog.dao.jdbc.JdbcPostTagDao;
import com.tchepannou.blog.dao.jdbc.JdbcTagDao;
import com.tchepannou.blog.service.command.CreatePostCommand;
import com.tchepannou.blog.service.command.DeletePostCommand;
import com.tchepannou.blog.service.command.GetPostCommand;
import com.tchepannou.blog.service.command.GetPostListCommand;
import com.tchepannou.blog.service.command.ReblogPostCommand;
import com.tchepannou.blog.service.command.SearchCommand;
import com.tchepannou.blog.service.command.UpdatePostCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
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

    //-- JMS Config
    @Bean
    JmsListenerContainerFactory jmsContainerFactory(ConnectionFactory connectionFactory) {
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean
    JmsTemplate eventLogQueue(ConnectionFactory factory){
        return new JmsTemplate(factory);
    }


    //-- DAO
    @Bean
    AttachmentDao attachmentDao () {
        return new JdbcAttachmentDao(dataSource());
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

    //-- Commands
    @Bean
    GetPostCommand getPostCommand(){
        return new GetPostCommand();
    }

    @Bean
    GetPostListCommand getPostListCommand(){
        return new GetPostListCommand();
    }

    @Bean CreatePostCommand createTextCommand () {
        return new CreatePostCommand();
    }

    @Bean UpdatePostCommand updateTextCommand(){
        return new UpdatePostCommand();
    }

    @Bean
    DeletePostCommand deletePostCommand () {
        return new DeletePostCommand();
    }

    @Bean
    ReblogPostCommand reblogPostCommand () {
        return new ReblogPostCommand();
    }

    @Bean
    SearchCommand searchCommand () {
        return new SearchCommand();
    }
}
