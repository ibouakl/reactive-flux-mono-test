package fr.soat;

import fr.soat.domain.Movie;
import fr.soat.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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

    public static void main(String[] args) {
        SpringApplication.run(FluxServiceApplication.class, args);
    }

    private String randomGenre() {
        String[] genres = {"action", "drama", "horror"};
        return genres[new Random().nextInt(genres.length)];
    }
}



