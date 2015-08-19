package com.tchepannou.blog.service.impl;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.tchepannou.blog.Constants;
import com.tchepannou.blog.domain.LogEvent;
import com.tchepannou.blog.service.Command;
import com.tchepannou.blog.service.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.AuthenticationException;

import javax.annotation.Resource;
import java.util.Date;
import java.util.OptionalLong;

public abstract class AbstractCommand<I, O> implements Command<I, O> {
    //-- Attributes
    @Autowired
    private MetricRegistry metrics;

    @Autowired
    private Jackson2ObjectMapperBuilder jackson;

    @Resource
    private JmsTemplate jmsTemplate;

    //-- Constructor
    public AbstractCommand(){
    }

    protected AbstractCommand(MetricRegistry metrics, JmsTemplate jmsTemplate, Jackson2ObjectMapperBuilder jackson){
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
            O result = doExecute(request, context);

            /* post */
            logEvent(request, context);
            return result;
        } catch (RuntimeException e) {
            metrics.meter(metricName + ".errors").mark();

            throw e;
        }
    }

    //-- Protected
    protected void authenticate (final CommandContext context) throws AuthenticationException {
    }

    protected String getLogEventName () {
        return null;
    }

    protected void logEvent (I request, CommandContext context) {
        String name = getLogEventName();
        if (Strings.isNullOrEmpty(name)){
            return;
        }

        LogEvent event = new LogEvent();
        event.setDate(new Date());
        event.setName(name);
        event.setPostId(context.getId());
        event.setBlogId(context.getBlogId());

        if (!isAnonymousUser()) {
            event.setUserId(getUserId().getAsLong());
        }

        if (request != null && !(request instanceof Void)){
            try {
                event.setRequest(jackson.build().writeValueAsString(request));
            } catch (JsonProcessingException e){
                throw new RuntimeException("Unable to add an entry into the log", e);
            }
        }

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
