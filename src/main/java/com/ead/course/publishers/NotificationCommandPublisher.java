package com.ead.course.publishers;

import com.ead.course.dtos.NotificationCommandDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationCommandPublisher {

    @Autowired
    RabbitTemplate rabbitTemplate; //inicia a instancia do rabbitTemplate

    @Value(value = "${ead.broker.exchange.notificationCommandExchange}") //acessa a exchange do application
    private String notificationCommandExchange;

    @Value(value = "${ead.broker.key.notificationCommandKey}")
    private String notificationCommandKey; //acessa a routing key

    public void publishNotificationCommand(NotificationCommandDto notificationCommandDto) { //recebe o dto populado
        rabbitTemplate.convertAndSend(notificationCommandExchange, notificationCommandKey, notificationCommandDto); //exchange, rota e a mensagem
    }

}

