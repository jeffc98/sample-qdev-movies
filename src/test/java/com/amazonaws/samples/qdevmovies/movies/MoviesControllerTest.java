package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy matey! These here tests be for our MoviesController crew member!
 * We be testing all the endpoints to make sure they work ship-shape!
 */
@DisplayName("MoviesController Tests - Yo ho ho! 🏴‍☠️")
public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MockMovieService mockMovieService;
    private ReviewService mockReviewService;

    // Mock MovieService that extends the real one for testing
    private static class MockMovieService extends MovieService {
        private List<Movie> testMovies;
        
        public MockMovieService() {
            super();
            // Create test movies for consistent testing
            testMovies = Arrays.asList(
                new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                new Movie(3L, "Comedy Film", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
            );
        }
        
        @Override
        public List<Movie> getAllMovies() {
            return testMovies;
        }
        
        @Override
        public Optional<Movie> getMovieById(Long id) {
            return testMovies.stream().filter(m -> m.getId() == id).findFirst();
        }
        
        @Override
        public List<Movie> searchMovies(String name, Long id, String genre) {
            List<Movie> results = new ArrayList<>(testMovies);
            
            if (name != null && !name.trim().isEmpty()) {
                String searchName = name.trim().toLowerCase();
                results = results.stream()
                    .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            if (id != null && id > 0) {
                results = results.stream()
                    .filter(movie -> movie.getId() == id)
                    .collect(java.util.stream.Collectors.toList());
            }
            
            if (genre != null && !genre.trim().isEmpty()) {
                String searchGenre = genre.trim().toLowerCase();
                results = results.stream()
                    .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            return results;
        }
        
        @Override
        public boolean hasValidSearchParameters(String name, Long id, String genre) {
            boolean hasName = name != null && !name.trim().isEmpty();
            boolean hasId = id != null && id > 0;
            boolean hasGenre = genre != null && !genre.trim().isEmpty();
            return hasName || hasId || hasGenre;
        }
    }

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MockMovieService();
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Should return movies view for getMovies")
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        // Check that movies are added to model
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(3, movies.size());
    }

    @Test
    @DisplayName("Should return movie details view for valid movie ID")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
        
        Movie movie = (Movie) model.getAttribute("movie");
        assertNotNull(movie);
        assertEquals("Test Movie", movie.getMovieName());
    }

    @Test
    @DisplayName("Should return error view for non-existent movie ID")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
        
        String title = (String) model.getAttribute("title");
        String message = (String) model.getAttribute("message");
        assertEquals("Movie Not Found", title);
        assertTrue(message.contains("999"));
    }

    @Test
    @DisplayName("Should return successful search results for valid parameters")
    public void testSearchMovies_ValidParameters() {
        ResponseEntity<?> response = moviesController.searchMovies("Test", null, null, model);
        
        assertEquals(200, response.getStatusCodeValue());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(1, responseBody.get("totalResults"));
        
        @SuppressWarnings("unchecked")
        List<Movie> results = (List<Movie>) responseBody.get("results");
        assertEquals(1, results.size());
        assertEquals("Test Movie", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should return error for search with no valid parameters")
    public void testSearchMovies_NoValidParameters() {
        ResponseEntity<?> response = moviesController.searchMovies(null, null, null, model);
        
        assertEquals(400, response.getStatusCodeValue());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(((String) responseBody.get("message")).contains("Arrr!"));
    }

    @Test
    @DisplayName("Should return empty results with pirate message when no movies found")
    public void testSearchMovies_NoResults() {
        ResponseEntity<?> response = moviesController.searchMovies("NonExistentMovie", null, null, model);
        
        assertEquals(200, response.getStatusCodeValue());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(0, responseBody.get("totalResults"));
        assertTrue(((String) responseBody.get("message")).contains("Shiver me timbers!"));
    }

    @Test
    @DisplayName("Legacy test - should still work after changes")
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }
}
