package com.example.task;

import com.example.service.IAccostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableScheduling
@CommonsLog
public class AccostTask {
    final Date date = new Date("1997/10/08 20:15");
    @Autowired
    ObjectMapper mapper;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    IAccostService accostService;

    //    工作日每天18:00定时发送消息到rabbitmq
    @Scheduled(cron = "0 0/10 18-22 ? * MON-FRI")
    public void sendEmail() {
//        查询此刻开始是否已开始有搭讪数据
        long count = accostService.selectCount();
        if (count < 1) {
            log.info("发送消息:" + count);
            rabbitTemplate.convertAndSend("emailExchange", "acc.send.ema", operate());
        }
    }

    private Map<String, Object> operate() {
        long totalSec = (System.currentTimeMillis() - date.getTime()) / 1000;
        Map<String, Object> map = new HashMap<>();
        final long daySec = 60 * 60 * 24;
        map.put("day", totalSec / daySec);
        map.put("hour", totalSec % daySec / (60 * 60));
        map.put("minute", totalSec % daySec % (60 * 60) / 60);
        map.put("second", totalSec % daySec % (60 * 60) % 60);
        int i = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        map.put("template", i == Calendar.SUNDAY || i == Calendar.SATURDAY ? "weekend" : "weekday");
        map.put("subject", new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 【搭讪任务提醒】");
        return map;
    }
}
