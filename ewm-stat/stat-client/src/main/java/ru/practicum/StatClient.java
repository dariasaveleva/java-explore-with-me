package ru.practicum;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.ewm.dto.EndpointHitDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StatClient {
    private final WebClient webClient;

    public ResponseEntity<Object> post(EndpointHitDto endpointHitDto) {

        return webClient.post()
                    .uri("/hit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(endpointHitDto))
                    .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.CREATED)) {
                        return clientResponse.bodyToMono(Object.class)
                                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
                } else {
                        return clientResponse.createException()
                                .flatMap(Mono::error);
                    }

    })
                .block();
    }

    public ResponseEntity<Object> get(String start, String end,
                                       List<String> uris, Boolean unique) {

        String params = uris.stream().reduce("", (result, uri) -> result + "&uris=" + uri);
        return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/stats")
                            .queryParam("start", start)
                            .queryParam("end", end)
                            .query(params)
                            .queryParam("unique", unique)
                            .build())
                .exchangeToMono(clientResponse -> {
                    if(clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(Object.class)
                                .map(body -> ResponseEntity.ok().body(body));
                    } else {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                })
                .block();
    }

}