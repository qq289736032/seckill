package com.jisen.seckillweb.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


/**
 * @author jisen
 * @date 2019/7/7 12:51
 */

@Configuration
public class RabbitMQConfig {

    /**
     * 消息队列名
     */
    public static final String SECKILL_QUEUE = "seckill.queue";

     @Bean
     public Queue deviceData(@Qualifier("rabbitAdmin") RabbitAdmin rabbitAdmin) {
         Queue queue = new Queue(SECKILL_QUEUE);
         rabbitAdmin.declareQueue(queue);
         return queue;
     }

     @Bean("rabbitAdmin")
     public RabbitAdmin rabbitAdmin(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
     }

     @Bean(name = "connectionFactory")
     @Primary
     public ConnectionFactory
     connectionFactory(
             @Value("${spring.rabbitmq.host}") String host,
             @Value("${spring.rabbitmq.port}") int port,
             @Value("${spring.rabbitmq.username}") String username,
             @Value("${spring.rabbitmq.password}") String password) {
         CachingConnectionFactory connectionFactory = getCachingConnectionFactory(host, port, username, password);
         return connectionFactory;
     }

     private CachingConnectionFactory getCachingConnectionFactory(String host, int port, String username, String password) {
         CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
         cachingConnectionFactory.setHost(host);
         cachingConnectionFactory.setPort(port);
         cachingConnectionFactory.setUsername(username);
         cachingConnectionFactory.setPassword(password);

         return cachingConnectionFactory;
     }

     @Bean(name = "rabbitTemplate")
     @Primary
     public RabbitTemplate rabbitTemplate(@Qualifier("connectionFactory")
         ConnectionFactory connectionFactory) {
         RabbitTemplate connectionTemplate = new RabbitTemplate(connectionFactory);
         return connectionTemplate;
     }


}

