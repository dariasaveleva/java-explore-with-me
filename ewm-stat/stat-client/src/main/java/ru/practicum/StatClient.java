package ru.practicum;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.ewm.dto.EndpointHitDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StatClient {
    private final WebClient webClient;

    public ResponseEntity<Object> post(EndpointHitDto endpointHitDto) {
        try {
             webClient.post()
                    .uri("/hit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(endpointHitDto))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Object> get(String start, String end,
                                       List<String> uris, Boolean unique) {

        String params = uris.stream().reduce("", (result, uri) -> result + "&uris=" + uri);
        try {
            webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/stats")
                            .queryParam("start", start)
                            .queryParam("end", end)
                            .query(params)
                            .queryParam("unique", unique)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}