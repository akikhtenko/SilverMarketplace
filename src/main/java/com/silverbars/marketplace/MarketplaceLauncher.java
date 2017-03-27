package com.silverbars.marketplace;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class MarketplaceLauncher extends SpringBootServletInitializer {

    public static void main(String[] args) {
        new MarketplaceLauncher()
                .configure(new SpringApplicationBuilder(MarketplaceLauncher.class))
                .run(args);
    }

}
