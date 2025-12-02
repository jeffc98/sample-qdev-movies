package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Ahoy matey! This here crew member searches through our treasure chest of movies
     * using various criteria. Pass in yer search parameters and we'll find the finest
     * films that match yer desires!
     * 
     * @param name The movie name to search for (case-insensitive partial matching)
     * @param id The exact movie ID to find
     * @param genre The genre to filter by (case-insensitive partial matching)
     * @return A list of movies that match the search criteria, or an empty list if no treasures be found
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Ahoy! Searching for movies with name: '{}', id: {}, genre: '{}'", name, id, genre);
        
        List<Movie> searchResults = new ArrayList<>(movies);
        
        // Filter by movie name if provided - case insensitive partial matching
        if (name != null && !name.trim().isEmpty()) {
            String searchName = name.trim().toLowerCase();
            searchResults = searchResults.stream()
                .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
            logger.debug("After name filter '{}': {} movies remain", searchName, searchResults.size());
        }
        
        // Filter by exact ID if provided
        if (id != null && id > 0) {
            searchResults = searchResults.stream()
                .filter(movie -> movie.getId() == id)
                .collect(Collectors.toList());
            logger.debug("After ID filter '{}': {} movies remain", id, searchResults.size());
        }
        
        // Filter by genre if provided - case insensitive partial matching
        if (genre != null && !genre.trim().isEmpty()) {
            String searchGenre = genre.trim().toLowerCase();
            searchResults = searchResults.stream()
                .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                .collect(Collectors.toList());
            logger.debug("After genre filter '{}': {} movies remain", searchGenre, searchResults.size());
        }
        
        logger.info("Search completed! Found {} movies matching the criteria", searchResults.size());
        return searchResults;
    }

    /**
     * Arrr! This crew member searches for movies by name only - perfect for when ye
     * remember part of a movie title but can't recall the full name!
     * 
     * @param movieName The movie name to search for (case-insensitive partial matching)
     * @return A list of movies whose names contain the search term
     */
    public List<Movie> searchMoviesByName(String movieName) {
        if (movieName == null || movieName.trim().isEmpty()) {
            logger.warn("Empty movie name provided for search - returning empty treasure chest");
            return new ArrayList<>();
        }
        
        String searchTerm = movieName.trim().toLowerCase();
        List<Movie> results = movies.stream()
            .filter(movie -> movie.getMovieName().toLowerCase().contains(searchTerm))
            .collect(Collectors.toList());
            
        logger.info("Found {} movies with name containing '{}'", results.size(), searchTerm);
        return results;
    }

    /**
     * Batten down the hatches! This crew member finds all movies in a specific genre.
     * Perfect for when ye want to watch only action films or romantic tales!
     * 
     * @param genre The genre to filter by (case-insensitive partial matching)
     * @return A list of movies in the specified genre
     */
    public List<Movie> searchMoviesByGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            logger.warn("Empty genre provided for search - returning empty treasure chest");
            return new ArrayList<>();
        }
        
        String searchGenre = genre.trim().toLowerCase();
        List<Movie> results = movies.stream()
            .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
            .collect(Collectors.toList());
            
        logger.info("Found {} movies in genre containing '{}'", results.size(), searchGenre);
        return results;
    }

    /**
     * Ship shape method to validate search parameters and prevent any scurvy bugs!
     * 
     * @param name Movie name parameter
     * @param id Movie ID parameter  
     * @param genre Genre parameter
     * @return true if at least one valid search parameter is provided
     */
    public boolean hasValidSearchParameters(String name, Long id, String genre) {
        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasId = id != null && id > 0;
        boolean hasGenre = genre != null && !genre.trim().isEmpty();
        
        return hasName || hasId || hasGenre;
    }
}
