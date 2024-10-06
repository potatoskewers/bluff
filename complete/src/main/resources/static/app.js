$(document).ready(function() {
    console.log("Index page is ready");
    connect();
    $("#conversation").show();
    $("#greetings").html("");

    $("#send").click(function() {
        console.log("sending...")
        sendName();  // Send name instead of sendMessage
    });

    $("#send-private    ").click(function() {
        sendPrivateMessage();
    });


    $("#start-game").click(function() {
        startGame();
    });
});

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, (frame) => {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', (greeting) => {
            showGreeting(JSON.parse(greeting.body).content);
        });
        stompClient.subscribe('/user/topic/private-message', (greeting) => {
            showGreeting(JSON.parse(greeting.body).content);
        });
        stompClient.subscribe('/topic/new-round', (UUIDs) => {
            // console.log(UUIDs)
            // console.log(UUIDs.body)
            let playerIDString = JSON.parse(UUIDs.body)
            let playerIDs = playerIDString.playerList.toString().split(" ");
            // showPlayers(playerIDs);
            // console.log(playerIDs);
            //find a way to parse the uuids into a list a strings here.
        });
    });

    stompClient.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
    };

    stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendPrivateMessage() {
    const message = JSON.stringify({ name: $("#private-message").val() });
    console.log("Sending message: ", message);
    stompClient.send("/app/private-messages", {}, message);
    // stompClient.send({destination: "/app/private-messages", headers: {}, body: JSON.stringify({'name': $("#name").val()})
}


function sendName() {
    const message = JSON.stringify({ name: $("#name").val() });
    console.log("Sending message: ", message);
    stompClient.send("/app/hello", {}, message);
    //9.25.24 the reason why i couldn't get something to print was due to this bad documentation.
    //ensure that this is the right format to send and it will work.
}

function startGame() {
    const message = JSON.stringify({ name: "Test User" });
    stompClient.send("/app/start-game", {}, message);
}

function showPlayers(UUIDS) {
    $("#greetings").append("<tr><td>" + UUIDS + "</td></tr>")
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function showPlayers(UUIds) {
    for (let i = 0; i < UUIds.length; i++) {
        $('#greetings').append("<tr><td>" + UUIds[i] + "</td></tr>");
    }

}
