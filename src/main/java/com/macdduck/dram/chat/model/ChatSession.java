package com.macdduck.dram.chat.model;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {

    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute("PK")})
    private String pk;              // "USER#{userId}"

    @Getter(onMethod_ = {@DynamoDbSortKey, @DynamoDbAttribute("SK")})
    private String sk;              // "SESSION#{sessionId}"

    private String type;            // "session"
    private String title;
    private String createdAt;       // ISO-8601
    private String updatedAt;       // ISO-8601
    private Long ttl;               // Unix epoch
}
