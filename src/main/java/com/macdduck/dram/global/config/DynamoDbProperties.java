package com.macdduck.dram.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aws.dynamodb")
public class DynamoDbProperties {

    private String region;
    private String tableName;
    private String endpoint;
}
