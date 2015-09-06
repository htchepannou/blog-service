package com.tchepannou.blog.config;

import com.tchepannou.blog.dao.AttachmentDao;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.dao.jdbc.AttachmentDaoImpl;
import com.tchepannou.blog.dao.jdbc.JdbcPostDao;
import com.tchepannou.blog.dao.jdbc.JdbcPostEntryDao;
import com.tchepannou.blog.dao.jdbc.JdbcPostTagDao;
import com.tchepannou.blog.dao.jdbc.JdbcTagDao;
import com.tchepannou.blog.service.CreatePostCommand;
import com.tchepannou.blog.service.DeletePostCommand;
import com.tchepannou.blog.service.GetPostCommand;
import com.tchepannou.blog.service.GetPostListCommand;
import com.tchepannou.blog.service.ReblogPostCommand;
import com.tchepannou.blog.service.SearchCommand;
import com.tchepannou.blog.service.UpdatePostCommand;
import com.tchepannou.blog.service.command.CreatePostCommandImpl;
import com.tchepannou.blog.service.command.DeletePostCommandImpl;
import com.tchepannou.blog.service.command.GetPostCommandImpl;
import com.tchepannou.blog.service.command.GetPostListCommandImpl;
import com.tchepannou.blog.service.command.ReblogPostCommandImpl;
import com.tchepannou.blog.service.command.SearchCommandImpl;
import com.tchepannou.blog.service.command.UpdateTextCommandImpl;
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
        return new AttachmentDaoImpl(dataSource());
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
        return new GetPostCommandImpl();
    }

    @Bean
    GetPostListCommand getPostListCommand(){
        return new GetPostListCommandImpl();
    }

    @Bean CreatePostCommand createTextCommand () {
        return new CreatePostCommandImpl();
    }

    @Bean UpdatePostCommand updateTextCommand(){
        return new UpdateTextCommandImpl();
    }

    @Bean
    DeletePostCommand deletePostCommand () {
        return new DeletePostCommandImpl();
    }

    @Bean
    ReblogPostCommand reblogPostCommand () {
        return new ReblogPostCommandImpl();
    }

    @Bean
    SearchCommand searchCommand () {
        return new SearchCommandImpl();
    }
}
