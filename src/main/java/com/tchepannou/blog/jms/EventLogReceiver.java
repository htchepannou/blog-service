package com.tchepannou.blog.jms;

import com.tchepannou.blog.Constants;
import com.tchepannou.blog.dao.EventLogDao;
import com.tchepannou.blog.domain.EventLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.transaction.annotation.Transactional;

public class EventLogReceiver {
    @Autowired
    private EventLogDao dao;

    @JmsListener(destination = Constants.QUEUE_EVENT_LOG, containerFactory = "jmsContainerFactory")
    @Transactional
    public void receiveMessage(EventLog event) {
        dao.create(event);
    }
}
