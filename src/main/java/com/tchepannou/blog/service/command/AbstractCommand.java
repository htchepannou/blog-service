package com.tchepannou.blog.service.command;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.tchepannou.blog.Constants;
import com.tchepannou.blog.client.v1.PostRequest;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.domain.EventLog;
import com.tchepannou.blog.service.Command;
import com.tchepannou.blog.service.CommandContext;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.OptionalLong;

public abstract class AbstractCommand<I, O> implements Command<I, O> {
    //-- Attributes
    private Logger logger;  // NOSONAR

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

    protected abstract String getEventName();


    //-- Command Override
    @Override
    public O execute(I request, CommandContext context) {
        final String metricName = getMetricName();
        final Timer.Context timer = metrics.timer(metricName + "-duration").time();
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
        } finally {
            timer.stop();
        }
    }

    //-- Protected
    protected void authenticate (final CommandContext context) {
    }

    protected Logger getLogger () {
        return logger;
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

        if (request instanceof PostRequest) {
            event.setUserId(((PostRequest)request).getUserId());
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
                event.setRequest(String.format(
                        "%s%n%s",
                        e.getMessage(),
                        ExceptionUtils.getFullStackTrace(e)
                ));
            }
        }

        getLogger().info(String.format("Sending %s to %s", event, Constants.QUEUE_EVENT_LOG));
        jmsTemplate.send(Constants.QUEUE_EVENT_LOG, session -> session.createObjectMessage(event));
    }

    //-- Getter
    @Deprecated
    public boolean isAnonymousUser (){
        return !getUserId().isPresent();
    }

    @Deprecated
    public OptionalLong getUserId (){
        return OptionalLong.empty();
    }
}
