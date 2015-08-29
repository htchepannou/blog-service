package com.tchepannou.blog.jms;

import com.tchepannou.blog.client.v1.BlogConstants;
import com.tchepannou.blog.client.v1.PostEvent;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.transaction.annotation.Transactional;

public class PostEventReceiver {
    public static PostEvent lastEvent = null;

    @JmsListener(destination = BlogConstants.QUEUE_EVENT_LOG, containerFactory = "jmsContainerFactory")
    @Transactional
    public void receiveMessage(PostEvent event) {
        lastEvent = event;
    }
}
