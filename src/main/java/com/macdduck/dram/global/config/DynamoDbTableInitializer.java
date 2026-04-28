package com.macdduck.dram.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamoDbTableInitializer implements ApplicationRunner {

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbProperties properties;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        if (!StringUtils.hasText(properties.getEndpoint())) {
            return;
        }
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String tableName = properties.getTableName();

        try {
            dynamoDbClient.describeTable(r -> r.tableName(tableName));
            log.info("DynamoDB 테이블 '{}' 이미 존재합니다.", tableName);
        } catch (ResourceNotFoundException e) {
            dynamoDbClient.createTable(CreateTableRequest.builder()
                    .tableName(tableName)
                    .attributeDefinitions(
                            AttributeDefinition.builder().attributeName("PK").attributeType(ScalarAttributeType.S).build(),
                            AttributeDefinition.builder().attributeName("SK").attributeType(ScalarAttributeType.S).build()
                    )
                    .keySchema(
                            KeySchemaElement.builder().attributeName("PK").keyType(KeyType.HASH).build(),
                            KeySchemaElement.builder().attributeName("SK").keyType(KeyType.RANGE).build()
                    )
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build());

            dynamoDbClient.waiter().waitUntilTableExists(r -> r.tableName(tableName));
            log.info("DynamoDB 테이블 '{}' 생성 완료.", tableName);
        }
    }
}
