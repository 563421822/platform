package com.example.consumer;

import com.example.constant.EmailConstant;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
public class MqConsumer {
    final JavaMailSender mailSender;
    final TemplateEngine engine;


    @Autowired
    public MqConsumer(JavaMailSender mailSender, TemplateEngine engine) {
        this.mailSender = mailSender;
        this.engine = engine;
    }

    @RabbitListener(queues = "emailQueue")
    void getMessage(Message message) throws MessagingException, IOException, ClassNotFoundException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom(EmailConstant.EMAILFROM);
        helper.setTo(EmailConstant.EMAILTO);
        ByteArrayInputStream stream = new ByteArrayInputStream(message.getBody());
        ObjectInputStream inputStream = new ObjectInputStream(stream);
        var map = (Map<String, Object>) inputStream.readObject();
        Object temp = map.get(EmailConstant.TEMPLATE);
//        读取模板
        if (temp != null) process(map);
        helper.setSubject(temp != null ? map.get(EmailConstant.SUBJECT).toString() : new SimpleDateFormat("yy/M/d HH:mm:ss").format(new Date()) + "位置异常【logId】:" + map.get("logId"));
        helper.setText(String.valueOf(temp != null ? map.get(EmailConstant.TEXT).toString() : map + "<br/>" + message.getMessageProperties()), true);
        mailSender.send(mimeMessage);
    }

    void process(Map<String, Object> map) {
//        填充数据
        Context context = new Context();
        context.setVariables(map);
        map.put(EmailConstant.TEXT, engine.process(map.get(EmailConstant.TEMPLATE).toString(), context));
    }
}
