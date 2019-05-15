package fr.soat.controller;

import fr.soat.domain.Movie;
import fr.soat.domain.MovieEvent;
import fr.soat.service.MovieService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/movies")
class MovieController {

    private final MovieService movieService;

    MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public Flux<Movie> getAll() {
        return movieService.getAll();
    }

    @GetMapping("/{id}")
    public Mono<Movie> findById(@PathVariable String id) {
        return movieService.getMovieById(id);
    }

    @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MovieEvent> stream(@PathVariable String id) {
        return movieService.getMovieById(id).flatMapMany(movie -> movieService.stream(movie));
    }


}
