package com.api.gateway.runner;

import com.api.gateway.data.DestinationGenerator;
import com.api.gateway.transaction.model.Message;
import com.api.gateway.transaction.model.WebSocketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

@Component
public class WebSocketRunner implements ApplicationRunner {

    private final DestinationGenerator destinationGenerator = new DestinationGenerator();
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry userRegistry;

    @Autowired
    public WebSocketRunner(
            SimpMessagingTemplate messagingTemplate,
            SimpUserRegistry userRegistry
    ) {
        this.messagingTemplate = messagingTemplate;
        this.userRegistry = userRegistry;
    }

    // At the start of the application, the web socket connections are cleared
    public void run(ApplicationArguments args) {
        for (SimpUser user : userRegistry.getUsers()) {
            user.getSessions().forEach(session -> {
                messagingTemplate.convertAndSend(
                        destinationGenerator.getServerStatusDest(),
                        new Message("Server has restarted.", WebSocketStatus.SERVER_RESTARTED)
                );
            });
        }
    }

}
