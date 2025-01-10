package com.fantasy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Slf4j
@EnableTransactionManagement//开启对事务的支持
@EnableConfigurationProperties
public class FantasyGamesApplication {

    public static void main(String[] args) {
        SpringApplication.run(FantasyGamesApplication.class, args);
    }

}
