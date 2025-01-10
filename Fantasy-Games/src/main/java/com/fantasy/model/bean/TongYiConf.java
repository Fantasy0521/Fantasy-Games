package com.fantasy.model.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tongyi")
public class TongYiConf {

    private String apiKey;
    private String model;
    private String role;

}