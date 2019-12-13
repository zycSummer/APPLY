package com.jet.cloud.jetsndcxqyapply.common;

import com.jet.cloud.jetsndcxqyapply.mapper.DistrictCommitteeMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.DistrictMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * Java定时器
 * 每年1月1日更新复审结论置为空
 */
@Component
public class SendWsListener {
    private final static Logger logger = LoggerFactory.getLogger(SendWsListener.class);

    @Autowired
    public DistrictCommitteeMapper districtCommitteeMapper;

    @Scheduled(cron = "0 0 0 1 1 *")
    public void testTasks() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("year", Calendar.getInstance().get(Calendar.YEAR) - 1);
        Integer integer = districtCommitteeMapper.updateSiteReview(map);
        logger.info("定时任务执行时间：" + DateUtils.dateFormatYYMMDD() + "," + integer);
    }

    public static void main(String[] args) throws Exception {
        SendWsListener s = new SendWsListener();
        s.testTasks();
    }
}
