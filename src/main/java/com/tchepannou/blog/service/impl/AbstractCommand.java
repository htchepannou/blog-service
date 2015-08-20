package com.tchepannou.blog.service.impl;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.tchepannou.blog.Constants;
import com.tchepannou.blog.domain.EventLog;
import com.tchepannou.blog.rr.PostResponse;
import com.tchepannou.blog.service.Command;
import com.tchepannou.blog.service.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.AuthenticationException;

import javax.annotation.Resource;
import java.util.Date;
import java.util.OptionalLong;

public abstract class AbstractCommand<I, O> implements Command<I, O> {
    //-- Attributes
    private Logger logger;

    @Autowired
    private MetricRegistry metrics;

    @Autowired
    private Jackson2ObjectMapperBuilder jackson;

    @Resource
    private JmsTemplate jmsTemplate;

    //-- Constructor
    public AbstractCommand(){
        this.logger = LoggerFactory.getLogger(getClass());
    }

    protected AbstractCommand(MetricRegistry metrics, JmsTemplate jmsTemplate, Jackson2ObjectMapperBuilder jackson){
        this();

        this.metrics = metrics;
        this.jmsTemplate = jmsTemplate;
        this.jackson = jackson;
    }

    //-- Abstract
    protected abstract O doExecute (I request, CommandContext context);

    protected abstract String getMetricName ();

    //-- Command Override
    @Override
    public O execute(I request, CommandContext context) {
        final String metricName = getMetricName();
        try {
            metrics.meter(metricName).mark();

            /* pre */
            authenticate(context);

            /* execute */
            O response = doExecute(request, context);

            /* post */
            logEvent(request, response, context);
            return response;
        } catch (RuntimeException e) {
            metrics.meter(metricName + "-errors").mark();

            throw e;
        }
    }

    //-- Protected
    protected void authenticate (final CommandContext context) throws AuthenticationException {
    }

    protected Logger getLogger () {
        return logger;
    }


    protected String getEventName() {
        return null;
    }

    protected void logEvent (I request, O response, CommandContext context) {
        String name = getEventName();
        if (Strings.isNullOrEmpty(name)){
            return;
        }

        EventLog event = new EventLog();
        event.setCreated(new Date());
        event.setName(name);
        event.setBlogId(context.getBlogId());

        if (!isAnonymousUser()) {
            event.setUserId(getUserId().getAsLong());
        }

        if (response instanceof PostResponse) {
            event.setPostId(((PostResponse) response).getId());
        } else {
            event.setPostId(context.getId());
        }

        if (request != null && !(request instanceof Void)){
            try {
                event.setRequest(jackson.build().writeValueAsString(request));
            } catch (JsonProcessingException e){
                throw new RuntimeException("Unable to add an entry into the log", e);
            }
        }

        getLogger().info(String.format("Sending %s to %s", event, Constants.QUEUE_EVENT_LOG));
        jmsTemplate.send(Constants.QUEUE_EVENT_LOG, session -> session.createObjectMessage(event));
    }

    //-- Getter
    public boolean isAnonymousUser (){
        return !getUserId().isPresent();
    }

    public OptionalLong getUserId (){
        return OptionalLong.empty();
    }
}
