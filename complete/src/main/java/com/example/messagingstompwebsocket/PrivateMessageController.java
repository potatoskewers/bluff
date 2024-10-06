//package com.example.messagingstompwebsocket;
//
//import com.websocket.wstutorial.dto.Message;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.util.HtmlUtils;
//
//@RestController
//public class PrivateMessageController {
//
//    @Autowired
//    private WSService service;
//
//    @PostMapping("/send-message")
//    public Greeting sendMessage(@RequestBody final HelloMessage message) throws Exception{
//        Thread.sleep(1000); // simulated delay
//        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
//    }
//
//    @PostMapping("/send-private-message/{id}")
//    public void sendPrivateMessage(@PathVariable final String id,
//                                   @RequestBody final HelloMessage message) {
//    }
//}