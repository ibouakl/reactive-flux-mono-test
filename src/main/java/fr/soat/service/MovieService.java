package fr.soat.service;

import fr.soat.domain.Movie;
import fr.soat.domain.MovieEvent;
import fr.soat.repository.MovieRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.Date;
import java.util.Random;
import java.util.stream.Stream;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Mono<Movie> getMovieById(String id) {
        return movieRepository.findById(id);
    }

    public Flux<Movie> getAll() {
        return movieRepository.findAll();
    }

    public Flux<MovieEvent> stream(Movie movie) {
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(2));
        Flux<MovieEvent> movieEventFlux = Flux.fromStream(Stream.generate(() -> new MovieEvent(movie, new Date(), randomUser())));
        return Flux.zip(interval, movieEventFlux).map(Tuple2::getT2);
    }

    private String randomUser() {
        String[] users = {"user1", "user2 ", "user3"};
        return users[new Random().nextInt(users.length)];
    }
}
