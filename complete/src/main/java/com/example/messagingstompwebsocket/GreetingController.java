package com.example.messagingstompwebsocket;

import game.logic.*;
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
	private Round round;
	@Autowired
	public GreetingController(SimpMessagingTemplate brokerMessagingTemplate, MessagingService messagingService) {
		this.brokerMessagingTemplate = brokerMessagingTemplate;
        this.messagingService = messagingService;
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


	//here you need to handle a received gameID, and then map it with the sessionID that they have.



	@MessageMapping("/connect-game/{gameId}")
	public void addToGame(@DestinationVariable String gameId, HelloMessage message, final Principal principal, SimpMessageHeaderAccessor headerAccessor) throws Exception {
		System.out.println("Adding a user to the game with id: " + gameId + " and sessionID: " + headerAccessor.getSessionId());
		UUIDs.playerToGameAdder(headerAccessor.getSessionId(), gameId);
		if(UUIDs.getPlayerListToGameID().get(gameId) == null) {
			LinkedList<String> playerList = new LinkedList<>();
			playerList.add(principal.getName());
			UUIDs.getPlayerListToGameID().put(gameId, playerList);
		}
		else {
			LinkedList<String> playerList = UUIDs.getPlayerListToGameID().get(gameId);
			playerList.add(principal.getName());
			UUIDs.getPlayerListToGameID().put(gameId, playerList);
		}
		System.out.println("getting PlayerListToGameID() Which should contain principal UUIDs: " + UUIDs.getPlayerListToGameID());
		System.out.println("resulting hashmap: " + UUIDs.getPlayerToGame());
	}

	@MessageMapping("/private-messages/{sessionId}")
	public void getPrivateMessage(@DestinationVariable String sessionId, HelloMessage message, final Principal principal, SimpMessageHeaderAccessor headerAccessor) throws Exception {
		Thread.sleep(1000); // simulated delay
		logger.info("sent: " +  message.getName());
		if(message.getName().equals("bs")){
			String sessionID = headerAccessor.getSessionId();
//			System.out.println("sessionID: " + sessionID);
			String gameId = UUIDs.retrieveGameID(sessionID);
			System.out.println("loop tester");
//			System.out.println(UUIDs.getPlayerToGame());
//			System.out.println("gameId: " + gameId);
//			System.out.println(UUIDs.getGameIdentifier());
//			System.out.println(UUIDs.retrieveGame(gameId));
			LinkedList<String> PlayerList = UUIDs.getPlayerListToGameID().get(gameId);
//			Round.getCurrentThread().interrupt();
			System.out.println("handling BS clause: " + "challenger: " + sessionId);
//			String gameId = Game.getGameId();
			Game contestedGame = UUIDs.retrieveGame(gameId);
			contestedGame.getCurrentThread().interrupt();
			LinkedList<Card> pile = contestedGame.getPile();
			LinkedList<Card> playedCards = contestedGame.getClientPlayedCards();
			int currentPlayer = contestedGame.getCurrentPlayer();
			int firstPlayer = contestedGame.getFirstPlayer();
			Contest contest = new Contest(sessionID, messagingService,PlayerList, sessionId, pile, playedCards, contestedGame.getRule(), contestedGame.getPlayers(), currentPlayer, firstPlayer);
			contest.run();
			new Thread(contestedGame).start();
//			round = new Round(messagingService, gameId);package com.example.messagingstompwebsocket;
//
		}
		else {
			InputHandler.setPlayerInput(message.getName(), principal);
		}
		messagingTemplate.convertAndSend("/topic/new-round/" + sessionId, new Greeting("sent: " + HtmlUtils.htmlEscape(message.getName()) + "!"));

//			return new Greeting("sent: " + HtmlUtils.htmlEscape(message.getName()) + "!");
		}

	@MessageMapping("/start-game/{gameId}")
	public Greeting startingGame(@DestinationVariable String gameId, HelloMessage message) throws Exception {
//		String gameId = message.getName();
		String roomId = UUID.randomUUID().toString();
		System.out.println("Starting a new game with id: " + roomId);
		Thread.sleep(1000); // simulated delay
		LinkedList<String> PlayerList = UUIDs.getPlayerListToGameID().get(gameId);
		for(int i = 0; i < PlayerList.size(); i++){
			messagingService.sendPrivateMessage(PlayerList.get(i), "test" + i, gameId);
			convertedPlayerlist = convertedPlayerlist.concat(PlayerList.get(i) + " ");
		}
		 // Correct way
		Game game = new Game(PlayerList, messagingService, gameId);
		game.startingGame();
		UUIDs.gameIdentifierAdder(game, gameId);
		System.out.println("gameId: " + gameId + " game: " + game + " gameRooms: " + UUIDs.getGameIdentifier());
		//now, we can obtain the thread from finding the game mapping.
		new Thread(game).start();
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
