package com.example.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    //    创建exchange TOPIC
    @Bean
    TopicExchange getTopicExchange() {
        return new TopicExchange("emailExchange");
    }

    //    创建queue
    @Bean
    Queue getQueue() {
        return new Queue("emailQueue");
    }

    //    绑定队列和交换机
    @Bean
    Binding getBinding(TopicExchange exchange, Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with("*.send.*");
    }

}

