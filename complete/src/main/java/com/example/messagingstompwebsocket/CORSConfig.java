//package com.example.messagingstompwebsocket;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@EnableWebMvc
//public class CORSConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // Allow all endpoints
//                .allowedOrigins("ws://localhost:5173") // Allow requests from this origin
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow these methods
//                .allowedHeaders("*") // Allow all headers
//                .allowCredentials(true);
//    }
//}