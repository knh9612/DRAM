package com.macdduck.dram.chat.repository;

import com.macdduck.dram.chat.model.ChatMessage;
import com.macdduck.dram.chat.model.ChatSession;
import com.macdduck.dram.global.config.DynamoDbProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRepository {

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbProperties properties;

    private DynamoDbTable<ChatSession> sessionTable() {
        return enhancedClient.table(properties.getTableName(), TableSchema.fromBean(ChatSession.class));
    }

    private DynamoDbTable<ChatMessage> messageTable() {
        return enhancedClient.table(properties.getTableName(), TableSchema.fromBean(ChatMessage.class));
    }

    public List<ChatSession> getSessionsByUser(Long userId) {
        QueryConditional qc = QueryConditional.sortBeginsWith(
                Key.builder().partitionValue("USER#" + userId).sortValue("SESSION#").build()
        );
        return sessionTable().query(qc).items().stream().toList();
    }

    public Optional<ChatSession> findSession(Long userId, String sessionId) {
        return Optional.ofNullable(sessionTable().getItem(Key.builder()
                .partitionValue("USER#" + userId)
                .sortValue("SESSION#" + sessionId)
                .build()));
    }

    public void saveSession(ChatSession session) {
        sessionTable().putItem(session);
    }

    public void touchSession(Long userId, String sessionId) {
        String now = LocalDateTime.now().toString();
        long ttl = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        dynamoDbClient.updateItem(UpdateItemRequest.builder()
                .tableName(properties.getTableName())
                .key(Map.of(
                        "PK", AttributeValue.fromS("USER#" + userId),
                        "SK", AttributeValue.fromS("SESSION#" + sessionId)
                ))
                .updateExpression("SET updatedAt = :updatedAt, ttl = :ttl")
                .expressionAttributeValues(Map.of(
                        ":updatedAt", AttributeValue.fromS(now),
                        ":ttl", AttributeValue.fromN(String.valueOf(ttl))
                ))
                .build());
    }

    public void deleteSessionAndMessages(Long userId, String sessionId) {
        sessionTable().deleteItem(Key.builder()
                .partitionValue("USER#" + userId)
                .sortValue("SESSION#" + sessionId)
                .build());

        QueryConditional qc = QueryConditional.sortBeginsWith(
                Key.builder().partitionValue("SESSION#" + sessionId).sortValue("MSG#").build()
        );
        messageTable().query(qc).items().forEach(msg ->
                messageTable().deleteItem(Key.builder()
                        .partitionValue(msg.getPk())
                        .sortValue(msg.getSk())
                        .build())
        );
    }

    public void saveMessage(ChatMessage message) {
        messageTable().putItem(message);
    }

    public List<ChatMessage> getMessagesBySession(String sessionId) {
        QueryConditional qc = QueryConditional.sortBeginsWith(
                Key.builder().partitionValue("SESSION#" + sessionId).sortValue("MSG#").build()
        );
        return messageTable().query(qc).items().stream().toList();
    }

}
