package com.example.messagingstompwebsocket;

import game.logic.Contest;
import game.logic.Game;
import game.logic.InputHandler;
import game.logic.Round;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;



@Controller
@RestController
//@CrossOrigin(origins = "https://localhost:5173/game")
public class GreetingController {
	private final SimpMessagingTemplate brokerMessagingTemplate;
	private final MessagingService messagingService;
	String convertedPlayerlist = "";
	private final Map<String, Game> gameRooms = new ConcurrentHashMap<>();

	final Logger logger = LoggerFactory.getLogger(GreetingController.class);
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	private Game game;
	private Round round;
	@Autowired
	public GreetingController(SimpMessagingTemplate brokerMessagingTemplate, MessagingService messagingService) {
		this.brokerMessagingTemplate = brokerMessagingTemplate;
        this.messagingService = messagingService;
		this.game = game;
		this.round = round;
    }

//	@MessageMapping("/hello")
//	@SendTo("/topic/greetings")
//	public Greeting greeting(final HelloMessage message) throws Exception {
//		if (message == null || message.getName() == null || message.getName().isEmpty()) {
//			logger.warn("Received an invalid message: " + message);
//			return new Greeting("Invalid message format");
//		}
//		Thread.sleep(1000); // simulated delay
//		System.out.println("sent message.");
//		logger.info("sent: " +  message.getName());
//		return new Greeting("sent: " + HtmlUtils.htmlEscape(message.getName()) + "!");
//	}
	@MessageMapping("/private-messages/{sessionId}")
	public Greeting getPrivateMessage(@DestinationVariable String sessionId, HelloMessage message, final Principal principal, SimpMessageHeaderAccessor headerAccessor) throws Exception {
		Thread.sleep(1000); // simulated delay
		logger.info("sent: " +  message.getName());
		InputHandler.setPlayerInput(message.getName(), principal);
		if(message.getName().equals("bs")){
			String sessionID = headerAccessor.getSessionId();
			LinkedList<String> PlayerList = UUIDs.getUUIDs();
			Round.getCurrentThread().interrupt();
			System.out.println("handling BS clause: " + "challenger: " + sessionID);
			String gameId = Game.getGameId();
			Contest contest = new Contest(sessionID, messagingService,PlayerList, gameId);
			contest.run();
			round = new Round(messagingService, gameId);
			new Thread(round).start();
		}
		messagingTemplate.convertAndSend("/topic/new-round/" + sessionId, new Greeting("sent: " + HtmlUtils.htmlEscape(message.getName()) + "!"));

			return new Greeting("sent: " + HtmlUtils.htmlEscape(message.getName()) + "!");
		}

	@MessageMapping("/start-game/{gameId}")
	public Greeting startingGame(@DestinationVariable String gameId, HelloMessage message) throws Exception {
//		String gameId = message.getName();
		String roomId = UUID.randomUUID().toString();
		System.out.println("Starting a new game with id: " + roomId);
		Thread.sleep(1000); // simulated delay
		LinkedList<String> PlayerList = UUIDs.getUUIDs();
		for(int i = 0; i < PlayerList.size(); i++){
			messagingService.sendPrivateMessage(PlayerList.get(i), "test" + i, gameId);
			convertedPlayerlist = convertedPlayerlist.concat(PlayerList.get(i) + " ");
		}
		 // Correct way
		game = new Game(PlayerList, messagingService, gameId);
		gameRooms.put(roomId, game);
		new Thread(game).start();
		logger.info("sent LinkedList" + convertedPlayerlist);
//		return new UUIDs(HtmlUtils.htmlEscape(convertedPlayerlist));
		messagingTemplate.convertAndSend("/topic/new-round/" + gameId, new Greeting(roomId));
		return new Greeting(roomId);
	}



//	@MessageMapping("/retrieve-cards")
//	@SendTo("/topic/display-cards")
//	public CardData displayCards(final HelloMessage message, final Principal principal, SimpMessageHeaderAccessor headerAccessor){
//		String sessionID = headerAccessor.getSessionId();
//		System.out.println("print sessionID" + sessionID);
//		String currentUUID = UUIDs.getUserIDs().get(sessionID);
//		System.out.println("Printing Current UUID:" + currentUUID);
//		Player currentPlayer = Player.getPlayerMapping().get(currentUUID);
//
//		LinkedList<Card> cards = currentPlayer.getPlayerCards();
//		LinkedList<String> PlayerList = UUIDs.getUUIDs();
//		System.out.println(cards.toString());
//		return new CardData(cards.toString(), PlayerList.size());
//	}

}

//MVC model controller
