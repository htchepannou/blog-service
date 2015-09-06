package com.tchepannou.blog.service.command;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import com.tchepannou.blog.client.v1.BlogConstants;
import com.tchepannou.blog.client.v1.PostEvent;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.service.Command;
import com.tchepannou.blog.service.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.Resource;

public abstract class AbstractCommand<I, O> implements Command<I, O> {
    //-- Attributes
    private Logger logger;  // NOSONAR

    @Autowired
    private MetricRegistry metrics;

    @Resource
    private JmsTemplate jmsTemplate;

    //-- Constructor
    public AbstractCommand(){
        this.logger = LoggerFactory.getLogger(getClass());
    }

    protected AbstractCommand(MetricRegistry metrics, JmsTemplate jmsTemplate){
        this();

        this.metrics = metrics;
        this.jmsTemplate = jmsTemplate;
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

            /* execute */
            O response = doExecute(request, context);

            /* post */
            logEvent(response, context);
            return response;
        } catch (RuntimeException e) {
            metrics.meter(metricName + "-errors").mark();

            throw e;
        } finally {
            timer.stop();
        }
    }

    //-- Protected
    protected Logger getLogger () {
        return logger;
    }


    protected void logEvent (O response, CommandContext context) {
        String name = getEventName();
        if (Strings.isNullOrEmpty(name)){
            return;
        }

        long id = context.getId();
        if (id <= 0 && response instanceof PostResponse){
            id = ((PostResponse)response).getId();
        }
        if (id > 0) {
            PostEvent event = new PostEvent(id, context.getBlogId(), name, context.getTransactionId());
            getLogger().info(String.format("Sending %s to %s", event, BlogConstants.QUEUE_EVENT_LOG));
            jmsTemplate.send(BlogConstants.QUEUE_EVENT_LOG, session -> session.createObjectMessage(event));
        }
    }
}
