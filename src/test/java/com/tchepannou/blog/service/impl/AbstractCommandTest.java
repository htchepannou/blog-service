package com.tchepannou.blog.service.impl;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.tchepannou.blog.service.CommandContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCommandTest {
    @Mock
    private MetricRegistry metrics;

    @Mock
    private Meter successMeter;

    @Mock
    private Meter errorMeter;

    @Test
    public void testExecute() throws Exception {
        // Given
        String name = "double";
        when(metrics.meter(name)).thenReturn(successMeter);
        when(metrics.meter(name + ".errors")).thenReturn(errorMeter);

        // When
        long result = new DoubleCommand(name).execute(1L, null);

        // Then
        assertThat(result).isEqualTo(2L);
        verify(successMeter).mark();
        verify(errorMeter, never()).mark();
    }

    @Test
    public void testExecute_Failure() throws Exception {
        // Given
        String name = "exception";
        when(metrics.meter(name)).thenReturn(successMeter);
        when(metrics.meter(name + ".errors")).thenReturn(errorMeter);

        try {
            // When
            new ExceptionCommand(name).execute(1L, null);
            fail("failed");
        } catch (RuntimeException e) {
            // Then
            verify(successMeter).mark();
            verify(errorMeter).mark();
        }
    }

    //-- Inner classes
    private class DoubleCommand extends AbstractCommand<Long, Long> {
        private String name;

        public DoubleCommand(String name){
            super(metrics);
            this.name = name;
        }

        @Override
        protected Long doExecute(Long input, CommandContext ctx) {
            return 2*input;
        }

        @Override
        protected String getMetricName() {
            return name;
        }
    }

    private class ExceptionCommand extends AbstractCommand<Long, Long> {
        private String name;

        public ExceptionCommand(String name){
            super(metrics);
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
    }
}
