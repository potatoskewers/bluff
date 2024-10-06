package com.example.messagingstompwebsocket;

import game.logic.Contest;
import game.logic.Game;
import game.logic.InputHandler;
import game.logic.Round;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.util.LinkedList;


@Controller
public class GreetingController {
	private final SimpMessagingTemplate brokerMessagingTemplate;
	private final MessagingService messagingService;
	String convertedPlayerlist = "";

	final Logger logger = LoggerFactory.getLogger(GreetingController.class);

	private Game game;
	private Round round;
	@Autowired
	public GreetingController(SimpMessagingTemplate brokerMessagingTemplate, MessagingService messagingService) {
		this.brokerMessagingTemplate = brokerMessagingTemplate;
        this.messagingService = messagingService;
		this.game = game;
		this.round = round;
    }

	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public Greeting greeting(final HelloMessage message) throws Exception {
		if (message == null || message.getName() == null || message.getName().isEmpty()) {
			logger.warn("Received an invalid message: " + message);
			return new Greeting("Invalid message format");
		}
		Thread.sleep(1000); // simulated delay
		System.out.println("sent message.");
		logger.info("sent: " +  message.getName());
		return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");

	}
	@MessageMapping("/private-messages")
	@SendToUser("/topic/private-message")
	public Greeting getPrivateMessage(final HelloMessage message, final Principal principal, SimpMessageHeaderAccessor headerAccessor) throws Exception {
		Thread.sleep(1000); // simulated delay
		logger.info("sent: " +  message.getName());
		InputHandler.setPlayerInput(message.getName(), principal);
		if(message.getName().equals("bs")){
			String sessionID = headerAccessor.getSessionId();
			LinkedList<String> PlayerList = UUIDs.getUUIDs();
			Round.getCurrentThread().interrupt();
			Contest contest = new Contest(sessionID, messagingService,PlayerList);
			contest.run();
			round = new Round(messagingService);
			new Thread(round).start();
		}
		return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
	}

	@MessageMapping("/start-game")
	@SendTo("/topic/new-round")
	public UUIDs startingGame(final HelloMessage message) throws Exception {
		Thread.sleep(1000); // simulated delay
		LinkedList<String> PlayerList = UUIDs.getUUIDs();
		for(int i = 0; i < PlayerList.size(); i++){
			messagingService.sendPrivateMessage(PlayerList.get(i), "test" + i);
			convertedPlayerlist = convertedPlayerlist.concat(PlayerList.get(i) + " ");
		}
		 // Correct way
		game = new Game(PlayerList, messagingService);
		new Thread(game).start();
		logger.info("sent LinkedList" + convertedPlayerlist);
		return new UUIDs(HtmlUtils.htmlEscape(convertedPlayerlist));

	}
}

//MVC model controller
