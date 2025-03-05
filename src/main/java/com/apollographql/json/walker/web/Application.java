package com.apollographql.json.walker.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class Application {

  @Configuration
  public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true);
      config.addAllowedOriginPattern("*"); // Allow all origins
      config.addAllowedHeader("*"); // Allow all headers
      config.addAllowedMethod("*"); // Allow all methods (GET, POST, etc.)
      source.registerCorsConfiguration("/**", config);
      return new CorsFilter(source);
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}