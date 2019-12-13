package com.jet.cloud.jetsndcxqyapply.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import java.time.format.DateTimeFormatter;

public class LogFilter extends Filter<ILoggingEvent> {

    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public FilterReply decide(ILoggingEvent event) {

       /* if (NettyStartConfig.sendInfo()) {
            LoggerMessage log = new LoggerMessage(event.getFormattedMessage(), dateTimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getTimeStamp()), ZoneId.systemDefault())), event.getThreadName(), event.getLoggerName(),
                    event.getLevel().levelStr, Constant.ip, "");
            ActiveMessage activeMessage = new ActiveMessage();
            activeMessage.setMessage(JSONUtil.poToJson(log));
            activeMessage.setType(MessageType.LOG.getType());
            InitNetty.pushTopicMessage(activeMessage);
        }*/
        return FilterReply.ACCEPT;
    }
}
