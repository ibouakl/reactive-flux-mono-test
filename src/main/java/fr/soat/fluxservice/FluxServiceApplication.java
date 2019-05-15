package fr.soat.fluxservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class FluxServiceApplication {

    @Bean
    CommandLineRunner commandLineRunner(MovieRepository movieRepository) {
        return (args) -> {
            movieRepository.deleteAll().subscribe(null
                    , null
                    , () -> Stream.of("Movie 1 ", " movie 2", "movie 3", "movie 4")
                            .map(title -> new Movie(UUID.randomUUID().toString(), title, randomGenre()))
                            .forEach(m -> movieRepository.save(m).subscribe(System.out::println)));


        };
    }

    private String randomGenre() {
        String[] genres = {"action", "drama", "horrror"};
        return genres[new Random().nextInt(genres.length)];
    }

    public static void main(String[] args) {
        SpringApplication.run(FluxServiceApplication.class, args);
    }
}


@RestController
@RequestMapping("/movies")
class MovieController {

    private final MovieService movieService;

    MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    Flux<Movie> getAll() {
        return movieService.getAll();
    }

    @GetMapping("/{id}")
    Mono<Movie> findById(@PathVariable String id) {
        return movieService.getMovieById(id);
    }

    @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<MovieEvent> stream(@PathVariable String id) {
        return movieService.getMovieById(id).flatMapMany(movie -> movieService.stream(movie));
    }


}

@Service
class MovieService {
    private final MovieRepository movieRepository;

    MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    Flux<MovieEvent> stream(Movie movie) {
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(2));
        Flux<MovieEvent> movieEventFlux = Flux.fromStream(Stream.generate(() -> new MovieEvent(movie, new Date(), randomUser())));
        return Flux.zip(interval, movieEventFlux).map(Tuple2::getT2);

    }




    Mono<Movie> getMovieById(String id) {
        return movieRepository.findById(id);
    }

    Flux<Movie> getAll() {
        return movieRepository.findAll();
    }

    private String randomUser() {
        String[] users = {"user1", "user2 ", "user3"};
        return users[new Random().nextInt(users.length)];
    }
}

interface MovieRepository extends ReactiveMongoRepository<Movie, String> {

}


@Document
@NoArgsConstructor
@AllArgsConstructor
@Data
class MovieEvent {
    private Movie movie;
    private Date date;
    private String user;
}

@Document
@NoArgsConstructor
@AllArgsConstructor
@Data
class Movie {

    @Id
    private String id;
    private String title;
    private String genre;
}
