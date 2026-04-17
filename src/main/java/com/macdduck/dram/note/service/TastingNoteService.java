package com.macdduck.dram.note.service;

import com.macdduck.dram.global.config.OpenAiProperties;
import com.macdduck.dram.global.enums.FlavorTag;
import com.macdduck.dram.note.dto.AiNoteRequest;
import com.macdduck.dram.whisky.entity.Whisky;
import com.macdduck.dram.whisky.service.WhiskyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TastingNoteService {

    private final WhiskyService whiskyService;
    private final OpenAiProperties openAiProperties;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.openai.com")
            .build();

    public String generateAiNote(AiNoteRequest request) {
        Whisky whisky = whiskyService.getWhisky(request.whiskyId());
        String prompt = buildPrompt(whisky, request);
        return callChatCompletion(prompt);
    }

    private String buildPrompt(Whisky whisky, AiNoteRequest request) {
        String tags = request.selectedTags().stream()
                .map(FlavorTag::getDisplayName)
                .collect(Collectors.joining(", "));

        return String.format("""
                당신은 위스키 테이스팅 전문가입니다. 다음 정보를 바탕으로 위스키 테이스팅 노트를 한국어로 작성해주세요.

                위스키: %s (%s)
                음용 방식: %s
                선택한 향미 태그: %s
                바디감: %s
                여운: %s

                NOSE, PALATE, FINISH 세 섹션으로 나눠서 각 2-3문장으로 작성해주세요.
                아래 형식을 반드시 지켜주세요:

                NOSE
                (향에 대한 설명)

                PALATE
                (맛에 대한 설명)

                FINISH
                (여운에 대한 설명)
                """,
                whisky.getNameKo(),
                whisky.getNameEn(),
                request.drinkingMethod().getDisplayName(),
                tags,
                request.body().getDisplayName(),
                request.aftertaste().getDisplayName()
        );
    }

    private String callChatCompletion(String prompt) {
        Map<String, Object> body = Map.of(
                "model", openAiProperties.getChatModel(),
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        var response = restClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + openAiProperties.getApiKey())
                .header("Content-Type", "application/json")
                .body(body)
                .retrieve()
                .body(ChatCompletionResponse.class);

        return response.choices().get(0).message().content();
    }

    private record ChatCompletionResponse(List<Choice> choices) {}
    private record Choice(Message message) {}
    private record Message(String content) {}
}