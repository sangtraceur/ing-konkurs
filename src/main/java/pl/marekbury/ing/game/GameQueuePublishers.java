package pl.marekbury.ing.game;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public record GameQueuePublishers(Mono<Integer> groupCount, Flux<Clan> clans) {}
