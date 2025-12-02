package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy matey! These here tests be for our MovieService treasure chest!
 * We be testing all the search functionality to make sure no scurvy bugs
 * sneak into our code like barnacles on a ship's hull!
 */
@DisplayName("MovieService Search Tests - Arrr! 🏴‍☠️")
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Should find movies by partial name match (case-insensitive)")
    public void testSearchMoviesByName_PartialMatch() {
        // Test searching for "Prison" should find "The Prison Escape"
        List<Movie> results = movieService.searchMoviesByName("Prison");
        
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    @DisplayName("Should find movies by name case-insensitive")
    public void testSearchMoviesByName_CaseInsensitive() {
        // Test case insensitive search
        List<Movie> results1 = movieService.searchMoviesByName("FAMILY");
        List<Movie> results2 = movieService.searchMoviesByName("family");
        List<Movie> results3 = movieService.searchMoviesByName("Family");
        
        assertEquals(1, results1.size());
        assertEquals(1, results2.size());
        assertEquals(1, results3.size());
        assertEquals("The Family Boss", results1.get(0).getMovieName());
        assertEquals("The Family Boss", results2.get(0).getMovieName());
        assertEquals("The Family Boss", results3.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should return empty list for non-existent movie name")
    public void testSearchMoviesByName_NotFound() {
        List<Movie> results = movieService.searchMoviesByName("NonExistentMovie");
        
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for null or empty movie name")
    public void testSearchMoviesByName_NullOrEmpty() {
        List<Movie> nullResults = movieService.searchMoviesByName(null);
        List<Movie> emptyResults = movieService.searchMoviesByName("");
        List<Movie> whitespaceResults = movieService.searchMoviesByName("   ");
        
        assertTrue(nullResults.isEmpty());
        assertTrue(emptyResults.isEmpty());
        assertTrue(whitespaceResults.isEmpty());
    }

    @Test
    @DisplayName("Should find movies by genre partial match")
    public void testSearchMoviesByGenre_PartialMatch() {
        // Test searching for "Drama" should find multiple movies
        List<Movie> results = movieService.searchMoviesByGenre("Drama");
        
        assertTrue(results.size() >= 3); // Should find at least Drama and Crime/Drama movies
        assertTrue(results.stream().anyMatch(m -> m.getMovieName().equals("The Prison Escape")));
        assertTrue(results.stream().anyMatch(m -> m.getMovieName().equals("The Family Boss")));
    }

    @Test
    @DisplayName("Should find movies by genre case-insensitive")
    public void testSearchMoviesByGenre_CaseInsensitive() {
        List<Movie> results1 = movieService.searchMoviesByGenre("ACTION");
        List<Movie> results2 = movieService.searchMoviesByGenre("action");
        List<Movie> results3 = movieService.searchMoviesByGenre("Action");
        
        assertEquals(results1.size(), results2.size());
        assertEquals(results2.size(), results3.size());
        assertTrue(results1.size() > 0); // Should find action movies
    }

    @Test
    @DisplayName("Should return empty list for non-existent genre")
    public void testSearchMoviesByGenre_NotFound() {
        List<Movie> results = movieService.searchMoviesByGenre("NonExistentGenre");
        
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for null or empty genre")
    public void testSearchMoviesByGenre_NullOrEmpty() {
        List<Movie> nullResults = movieService.searchMoviesByGenre(null);
        List<Movie> emptyResults = movieService.searchMoviesByGenre("");
        List<Movie> whitespaceResults = movieService.searchMoviesByGenre("   ");
        
        assertTrue(nullResults.isEmpty());
        assertTrue(emptyResults.isEmpty());
        assertTrue(whitespaceResults.isEmpty());
    }

    @Test
    @DisplayName("Should search movies with multiple criteria (name and genre)")
    public void testSearchMovies_MultipleParameters() {
        // Search for movies with "The" in name and "Drama" in genre
        List<Movie> results = movieService.searchMovies("The", null, "Drama");
        
        assertTrue(results.size() > 0);
        // All results should contain "The" in name and "Drama" in genre
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    @DisplayName("Should search movies by ID only")
    public void testSearchMovies_ByIdOnly() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should return empty list when searching by non-existent ID")
    public void testSearchMovies_ByNonExistentId() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return all movies when no search parameters provided")
    public void testSearchMovies_NoParameters() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        List<Movie> allMovies = movieService.getAllMovies();
        
        assertEquals(allMovies.size(), results.size());
    }

    @Test
    @DisplayName("Should validate search parameters correctly")
    public void testHasValidSearchParameters() {
        // Valid parameters
        assertTrue(movieService.hasValidSearchParameters("test", null, null));
        assertTrue(movieService.hasValidSearchParameters(null, 1L, null));
        assertTrue(movieService.hasValidSearchParameters(null, null, "Drama"));
        assertTrue(movieService.hasValidSearchParameters("test", 1L, "Drama"));
        
        // Invalid parameters
        assertFalse(movieService.hasValidSearchParameters(null, null, null));
        assertFalse(movieService.hasValidSearchParameters("", null, null));
        assertFalse(movieService.hasValidSearchParameters("   ", null, null));
        assertFalse(movieService.hasValidSearchParameters(null, 0L, null));
        assertFalse(movieService.hasValidSearchParameters(null, -1L, null));
        assertFalse(movieService.hasValidSearchParameters(null, null, ""));
        assertFalse(movieService.hasValidSearchParameters(null, null, "   "));
    }

    @Test
    @DisplayName("Should handle whitespace in search parameters")
    public void testSearchMovies_WithWhitespace() {
        // Test that leading/trailing whitespace is handled correctly
        List<Movie> results1 = movieService.searchMovies("  Prison  ", null, null);
        List<Movie> results2 = movieService.searchMovies("Prison", null, null);
        
        assertEquals(results1.size(), results2.size());
        if (!results1.isEmpty()) {
            assertEquals(results1.get(0).getMovieName(), results2.get(0).getMovieName());
        }
    }

    @Test
    @DisplayName("Should find movies with complex genre strings")
    public void testSearchMovies_ComplexGenre() {
        // Test searching in genres like "Crime/Drama"
        List<Movie> crimeResults = movieService.searchMoviesByGenre("Crime");
        List<Movie> dramaResults = movieService.searchMoviesByGenre("Drama");
        
        assertTrue(crimeResults.size() > 0);
        assertTrue(dramaResults.size() > 0);
        
        // Some movies should appear in both results (Crime/Drama movies)
        boolean hasOverlap = crimeResults.stream()
            .anyMatch(crimeMovie -> dramaResults.stream()
                .anyMatch(dramaMovie -> dramaMovie.getId() == crimeMovie.getId()));
        assertTrue(hasOverlap);
    }

    @Test
    @DisplayName("Should maintain existing functionality - getAllMovies")
    public void testGetAllMovies_StillWorks() {
        List<Movie> allMovies = movieService.getAllMovies();
        
        assertNotNull(allMovies);
        assertTrue(allMovies.size() > 0);
        // Should have at least the movies we know exist
        assertTrue(allMovies.size() >= 12);
    }

    @Test
    @DisplayName("Should maintain existing functionality - getMovieById")
    public void testGetMovieById_StillWorks() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        
        assertTrue(movie.isPresent());
        assertEquals("The Prison Escape", movie.get().getMovieName());
        
        Optional<Movie> nonExistent = movieService.getMovieById(999L);
        assertFalse(nonExistent.isPresent());
        
        Optional<Movie> nullId = movieService.getMovieById(null);
        assertFalse(nullId.isPresent());
    }
}