package com.tchepannou.blog.service.impl;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.tchepannou.blog.Constants;
import com.tchepannou.blog.controller.CommandContextImpl;
import com.tchepannou.blog.service.CommandContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import java.util.OptionalLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCommandTest {
    @Mock
    private MetricRegistry metrics;

    @Mock
    private Meter successMeter;

    @Mock
    private Meter errorMeter;

    @Mock JmsTemplate jmsTemplate;

    Jackson2ObjectMapperBuilder jackson = new Jackson2ObjectMapperBuilder();

    @Test
    public void testExecute() throws Exception {
        // Given
        String name = "double";
        when(metrics.meter(name)).thenReturn(successMeter);
        when(metrics.meter(name + ".errors")).thenReturn(errorMeter);

        // When
        long result = new DoubleCommand(name, 1).execute(
                1L,
                new CommandContextImpl().withBlogId(1).withId(100)
        );

        // Then
        assertThat(result).isEqualTo(2L);
        verify(successMeter).mark();
        verify(errorMeter, never()).mark();

        ArgumentCaptor<String> queue = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MessageCreator> creator = ArgumentCaptor.forClass(MessageCreator.class);
        verify(jmsTemplate).send(queue.capture(), creator.capture());
        assertThat(queue.getValue()).isEqualTo(Constants.QUEUE_EVENT_LOG);
    }



    @Test
    public void testExecute_Failure() throws Exception {
        // Given
        String name = "exception";
        when(metrics.meter(name)).thenReturn(successMeter);
        when(metrics.meter(name + ".errors")).thenReturn(errorMeter);

        try {
            // When
            new ExceptionCommand(name).execute(1L,
                new CommandContextImpl().withBlogId(1).withId(100)
            );
            fail("failed");
        } catch (RuntimeException e) {
            // Then
            verify(successMeter).mark();
            verify(errorMeter).mark();

            ArgumentCaptor<String> queue = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<MessageCreator> creator = ArgumentCaptor.forClass(MessageCreator.class);
            verify(jmsTemplate, never()).send(queue.capture(), creator.capture());
        }
    }

    //-- Inner classes
    private class DoubleCommand extends AbstractCommand<Long, Long> {
        private String name;
        private long userId;

        public DoubleCommand(String name, long userId){
            super(metrics, jmsTemplate, jackson);
            this.name = name;
            this.userId = userId;
        }

        @Override
        protected Long doExecute(Long input, CommandContext ctx) {
            return 2*input;
        }

        @Override
        protected String getMetricName() {
            return name;
        }

        protected String getLogEventName () {
            return name;
        }

        @Override
        public OptionalLong getUserId() {
            return OptionalLong.of(userId);
        }
    }

    private class ExceptionCommand extends AbstractCommand<Long, Long> {
        private String name;

        public ExceptionCommand(String name){
            super(metrics, jmsTemplate, jackson);
            this.name = name;
        }

        @Override
        protected Long doExecute(Long input, CommandContext ctx) {
            throw new RuntimeException("Failed");
        }

        @Override
        protected String getMetricName() {
            return name;
        }

        protected String getLogEventName () {
            return name;
        }
    }
}
