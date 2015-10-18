package com.tchepannou.blog.service.command;

import com.google.common.base.Strings;
import com.tchepannou.blog.client.v1.BlogConstants;
import com.tchepannou.blog.client.v1.PostEvent;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.service.Command;
import com.tchepannou.blog.service.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.Resource;

public abstract class AbstractCommand<I, O> implements Command<I, O> {
    //-- Attributes
    private Logger logger;  // NOSONAR

    @Resource
    private JmsTemplate jmsTemplate;

    //-- Constructor
    public AbstractCommand(){
        this.logger = LoggerFactory.getLogger(getClass());
    }

    protected AbstractCommand(JmsTemplate jmsTemplate){
        this();

        this.jmsTemplate = jmsTemplate;
    }

    //-- Abstract
    protected abstract O doExecute (I request, CommandContext context);

    protected String getEventName() {
        return null;
    }


    //-- Command Override
    @Override
    public O execute(I request, CommandContext context) {
        /* execute */
        O response = doExecute(request, context);

        /* post */
        logEvent(response, context);
        return response;
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
