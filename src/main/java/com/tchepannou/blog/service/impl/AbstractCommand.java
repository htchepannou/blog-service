package com.tchepannou.blog.service.impl;

import com.codahale.metrics.MetricRegistry;
import com.tchepannou.blog.service.Command;
import com.tchepannou.blog.service.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;

import java.util.OptionalLong;

public abstract class AbstractCommand<I, O> implements Command<I, O> {
    //-- Attributes
    @Autowired
    private MetricRegistry metrics;

    //-- Constructor
    public AbstractCommand(){
    }

    protected AbstractCommand(MetricRegistry metrics){
        this.metrics = metrics;
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

            authenticate(context);

            return doExecute(request, context);

        } catch (RuntimeException e) {
            metrics.meter(metricName + ".errors").mark();

            throw e;
        }
    }

    //-- Protected
    protected void authenticate (final CommandContext context) throws AuthenticationException {
    }

    //-- Getter
    public OptionalLong getUserId (){
        return OptionalLong.empty();
    }
}
