package com.stateless.stateless.player.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/player/skins/**")
                .addResourceLocations("classpath:/static/player/skins/");
        registry.addResourceHandler("/player/js/**")
                .addResourceLocations("classpath:/static/player/js/");
    }
}