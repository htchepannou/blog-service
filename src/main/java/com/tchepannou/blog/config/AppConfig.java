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
import com.tchepannou.blog.service.command.CreateCommand;
import com.tchepannou.blog.service.command.DeleteCommand;
import com.tchepannou.blog.service.command.GetCollectionCommand;
import com.tchepannou.blog.service.command.GetCommand;
import com.tchepannou.blog.service.command.ReblogCommand;
import com.tchepannou.blog.service.command.SearchCommand;
import com.tchepannou.blog.service.command.UpdateCommand;
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
    @Bean GetCommand getPostCommand(){
        return new GetCommand();
    }

    @Bean GetCollectionCommand getPostListCommand(){
        return new GetCollectionCommand();
    }

    @Bean CreateCommand createTextCommand () {
        return new CreateCommand();
    }

    @Bean UpdateCommand updateTextCommand(){
        return new UpdateCommand();
    }

    @Bean DeleteCommand deletePostCommand () {
        return new DeleteCommand();
    }

    @Bean ReblogCommand reblogPostCommand () {
        return new ReblogCommand();
    }

    @Bean
    SearchCommand searchCommand () {
        return new SearchCommand();
    }
}
