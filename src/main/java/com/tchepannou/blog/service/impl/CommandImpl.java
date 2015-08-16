package com.tchepannou.blog.service.impl;

import com.codahale.metrics.MetricRegistry;
import com.tchepannou.blog.service.Command;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CommandImpl<I, O> implements Command<I, O> {
    //-- Attributes
    @Autowired
    private MetricRegistry metrics;

    //-- Public
    public CommandImpl (){

    }
    protected CommandImpl(MetricRegistry metrics){
        this.metrics = metrics;
    }

    //-- Abstract
    protected abstract O doExecute (I request);

    protected abstract String getMetricName ();

    //-- Command Override
    @Override
    public O execute(I request) {
        final String metricName = getMetricName();
        try {
            metrics.meter(metricName).mark();

            return doExecute(request);

        } catch (RuntimeException e) {
            metrics.meter(metricName + ".errors").mark();

            throw e;
        }
    }
}
