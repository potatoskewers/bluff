import {useEffect, useState} from "react";
import SockJS from "sockjs-client";
import {over} from "stompjs";
import { useParams } from "react-router-dom";
import {useNavigate} from "react-router-dom"

const Game = () => {
    const [inGame, setInGame] = useState(false);
    const [client, setClient] = useState(null);
    const [showCards, setCards] = useState(null);
    const [playedCards, setPlayedCards] = useState([]);
    const [message, setMessage] = useState([]);
    const [sentMessage, setSentMessage] = useState(null);
    const {gameId } = useParams();
    const navigate = useNavigate();

    useEffect(() => {
        const socket = new SockJS('/gs-guide-websocket');
        const client = over(socket);
        client.connect({}, () => {
            console.log('Connected');
            setClient(client)
                client.subscribe(`/topic/new-round/${gameId}`, (message) => {
                    //handle retrieving the gameID from the message
                    // const fetchedGameId = JSON.parse(message.body).content;
                    // setGameId(fetchedGameId)// Parse the gameId from the message
                    console.log("extracted gameID: " + gameId);
                    // navigate('/game/' + gameId); // Redirect user to /game/:gameId
                    setInGame(true);
                    console.log("setting InGame to true.")
                });
            client.subscribe(`/topic/greetings/${gameId}`, (greeting) => {
                // showGreeting(JSON.parse(greeting.body).content);
                try {const message = JSON.parse(greeting.body).content;
                    showMessage(message); // Set the cards received from th/topic/greetings/{gameId}e server
                    console.log("Message received: ", message);
                } catch (error) {
                    console.error("Error parsing incoming message:", error);
                }
            });
            client.subscribe(`/user/topic/private-message/${gameId}`, (greeting) => {
                console.log( "private: " + JSON.parse(greeting.body).content);
                try {const message = JSON.parse(greeting.body);
                    showMessage(message); // Set the cards received from the server
                    console.log("Message received: ", message);
                } catch (error) {
                    console.error("Error parsing incoming message:", error);
                }
            });

            client.subscribe(`/user/topic/display-cards/${gameId}`, (greeting) => {
                try {const cards = JSON.parse(greeting.body);
                setCards(cards); // Set the cards received from the server
                console.log("Cards received:", cards);
                } catch (error) {
                    console.error("Error parsing incoming message:", error);
                }

            });
        }

        , (error) => {
            console.error('Connection error: ', error);
        },

        );

        // Clean up the connection when the component unmounts
        return () => {
            if (client) {
                client.disconnect(() => {
                    console.log('Disconnected');
                });
            }
        };
    }, []);
    function showMessage(newMessage) {
        setMessage((prevMessage) => [...prevMessage, newMessage.content]); // Add the message to message[]
    }


    function startGame() {
        setInGame(true);
        const message = JSON.stringify({name: gameId});
        if (client) {
            client.send(`/app/start-game/${gameId}`, {}, message);
        }
    }

    function movePlayedCard(card) {
        // Remove the card from showCards and add it to playedCards
        const updatedShowCards = showCards.filter(c => c !== card); // Filter out the clicked card
        setPlayedCards([...playedCards, card]); // Add the clicked card to playedCards
        setCards(updatedShowCards);
        // Get the item to move
        // Append the item to the target container
        console.log(card);
        console.log("moving to playedCards!")
    }

    function moveShownHand(card) {
        // Remove the card from showCards and add it to playedCards
        const updatedCards = playedCards.filter(c => c !== card);
        setCards([...showCards, card]); // Add the clicked card to playedCards
        setPlayedCards(updatedCards);
        // Get the item to move
        // Append the item to the target container
        console.log(card);
        console.log("moving to playedCards!")
    }

    function bs() {
        if (client) {
            const messageBs = JSON.stringify({ name: "bs" })
            client.send("/app/private-messages", {}, messageBs);
        }
    }

    function playCards(playedCards){

        let i;
        for(i = 0; i < playedCards.length ; i++) {
            let [suit, rank] = playedCards[i].split(' ');
            if (suit === "♥") {
                suit = "h"
            }
            if (suit === "♦") {
                suit = "d"
            }
            if(suit === "♠") {
                suit = "s"
            }
            if(suit === "♣") {
                suit = "c"
            }
            if (rank === "J") {
                rank = "11"
            }
            if (rank === "Q") {
                rank = "12"
            }
            if (rank === "K") {
                rank = "13"
            }
            if (rank === "A") {
                rank = "1"
            }

            let sentCard = JSON.stringify({ name: (suit + rank) });
            client.send("/app/private-messages", {}, sentCard);
        }
        setPlayedCards([]);
    }

    function sendMessage() {
        const message = JSON.stringify({ name: sentMessage });
        console.log("Sending message: ", message);
        client.send("/app/private-messages", {}, message);
    }


    function Round() {
        return (
            <div>
                <div className="gameLogConstruction">
                    <div className="col-md-12">
                        <table id="conversation" className="table table-striped">
                            <thead>
                            <tr>
                                <th>Bluff card game</th>
                            </tr>
                            </thead>
                            <tbody>
                            {message.map((msg, index) => {
                                return (
                                    <div className="messageMap" key={index}>
                                        <span className="messageLog"> {msg} </span>
                                    </div>
                                )
                            })}
                            </tbody>
                        </table>
                        <input type="text" id="private-message" className="form-control" placeholder="Type Here"
                               onChange={(event) => setSentMessage(event.target.value)}/>
                        <button id="send" className="btn btn-default" type="button" onClick={() => sendMessage()}>Send
                        </button>
                    </div>
                </div>
                <button className="play" onClick={() => playCards(playedCards)}>click me to send cards!</button>
                <button className="bullshit" onClick={() => bs()}>click me to BS the last player!</button>
                <h1>Your Cards</h1>
                {playedCards ? (
                    <div className="playedCards">
                        <p>here are your played cards.</p>
                        {playedCards.map((card, index) => {
                            let [suit, rank] = card.split(' ');
                            if (suit === "♥" || suit === "♦") {
                                return (
                                    <div className="cardMap" key={index} onClick={() => moveShownHand(card)}>
                                            <span className="rank"
                                                  id="redRank">{rank}</span> {/* Display rank in a span */}
                                        <span className="suit"
                                              id="redSuit">{suit}</span> {/* Display suit in a separate span */}
                                        <span className="suitBottom"
                                              id="redSuit">{suit}</span> {/* Display suit in a separate span */}
                                        <span className="rankBottom"
                                              id="redRank">{rank}</span> {/* Display rank in a span */}
                                        <span className="suitMiddle"
                                              id="redSuit">{suit}</span> {/* Display suit in a separate span */}
                                    </div>
                                )
                            } else {
                                return (
                                    <div className="cardMap" key={index}
                                         onClick={() => moveShownHand(card)}>
                                            <span className="rank"
                                                  id="rank">{rank}</span> {/* Display rank in a span */}
                                        <span className="suit"
                                              id="suit">{suit}</span> {/* Display suit in a separate span */}
                                        <span className="suitBottom"
                                              id="suit">{suit}</span> {/* Display suit in a separate span */}
                                        <span className="rankBottom"
                                              id="rank">{rank}</span> {/* Display rank in a span */}
                                        <span className="suitMiddle"
                                              id="suit">{suit}</span> {/* Display suit in a separate span */}
                                    </div>
                                )
                            }
                        })}
                    </div>) : (
                    <p>this is where the cards should be displayed.</p>
                )}
                {/* Check if showCards is not null before rendering */}
                {showCards ? (
                    <div className="cardContainer">
                        {showCards.map((card, index) => {
                            // Split the card string into suit and rank
                            let [suit, rank] = card.split(' ');
                            if (suit === "♥" || suit === "♦") {
                                return (
                                    <div className="cardMap" key={index} onClick={() => movePlayedCard(card)}>
                                            <span className="rank"
                                                  id="redRank">{rank}</span> {/* Display rank in a span */}
                                        <span className="suit"
                                              id="redSuit">{suit}</span> {/* Display suit in a separate span */}
                                        <span className="suitBottom"
                                              id="redSuit">{suit}</span> {/* Display suit in a separate span */}
                                        <span className="rankBottom"
                                              id="redRank">{rank}</span> {/* Display rank in a span */}
                                        <span className="suitMiddle"
                                              id="redSuit">{suit}</span> {/* Display suit in a separate span */}
                                    </div>
                                )
                            } else {
                                return (
                                    <div className="cardMap" key={index} onClick={() => movePlayedCard(card)}>
                                            <span className="rank"
                                                  id="rank">{rank}</span> {/* Display rank in a span */}
                                        <span className="suit"
                                              id="suit">{suit}</span> {/* Display suit in a separate span */}
                                        <span className="suitBottom"
                                              id="suit">{suit}</span> {/* Display suit in a separate span */}
                                        <span className="rankBottom"
                                              id="rank">{rank}</span> {/* Display rank in a span */}
                                        <span className="suitMiddle"
                                              id="suit">{suit}</span> {/* Display suit in a separate span */}
                                    </div>
                                );
                            }
                        })}
                    </div>
                ) : (
                    <p>Loading cards...</p> // Display a loading message if cards are not yet received
                )}
            </div>)
    }

    function Lobby() {
        return (
            <div>
                <h1>Welcome to the Lobby!</h1>
                <p>please wait...</p>
                <button onClick={startGame}>Please click me to start the game!</button>
            </div>
        )
    }


    return (
        <div id='lobby'>
            {inGame ? (
                <div>
                    <Round/>
                </div>
            ) : (
                <Lobby/>
            )
            }
        </div>
    );
};
export default Game;