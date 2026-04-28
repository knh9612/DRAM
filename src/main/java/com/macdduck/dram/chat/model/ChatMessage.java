package com.macdduck.dram.chat.model;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.List;

@DynamoDbBean
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute("PK")})
    private String pk;              // "SESSION#{sessionId}"

    @Getter(onMethod_ = {@DynamoDbSortKey, @DynamoDbAttribute("SK")})
    private String sk;              // "MSG#{timestamp}"

    private String type;            // "message"
    private String role;            // "user" | "assistant"
    private String content;
    private List<Long> recommendedIds;
    private Long ttl;               // Unix epoch
}
