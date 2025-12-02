package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Ahoy matey! This here endpoint be the treasure map for searching through our
     * collection of fine films! Pass in yer search criteria and we'll find the
     * movies that match yer heart's desire!
     * 
     * Supports both JSON API calls (for ye developers) and HTML form submissions (for ye landlubbers)
     * 
     * @param name Movie name to search for (partial matching, case-insensitive)
     * @param id Exact movie ID to find
     * @param genre Genre to filter by (partial matching, case-insensitive)
     * @param model Spring model for HTML responses
     * @return JSON response for API calls, HTML view for browser requests
     */
    @GetMapping(value = "/movies/search", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_HTML_VALUE})
    @ResponseBody
    public ResponseEntity<?> searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Ahoy! Search request received - name: '{}', id: {}, genre: '{}'", name, id, genre);
        
        try {
            // Validate that at least one search parameter is provided
            if (!movieService.hasValidSearchParameters(name, id, genre)) {
                logger.warn("No valid search parameters provided - returning error response");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Arrr! Ye need to provide at least one search parameter, matey! Try searching by name, id, or genre.");
                errorResponse.put("results", List.of());
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Perform the search using our trusty MovieService crew member
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            // Prepare the response with pirate flair
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalResults", searchResults.size());
            response.put("results", searchResults);
            
            if (searchResults.isEmpty()) {
                response.put("message", "Shiver me timbers! No movies found matching yer search criteria. Try different terms, ye scallywag!");
            } else {
                response.put("message", String.format("Yo ho ho! Found %d treasure%s matching yer search!", 
                    searchResults.size(), searchResults.size() == 1 ? "" : "s"));
            }
            
            // Add search parameters to response for reference
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("name", name);
            searchParams.put("id", id);
            searchParams.put("genre", genre);
            response.put("searchParameters", searchParams);
            
            logger.info("Search completed successfully! Found {} movies", searchResults.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Blimey! An error occurred during movie search: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Batten down the hatches! Something went wrong with the search. Try again later, ye landlubber!");
            errorResponse.put("results", List.of());
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Arrr! This endpoint handles HTML form submissions from the search form on the movies page.
     * It performs the search and returns the movies page with filtered results.
     * 
     * @param name Movie name to search for
     * @param id Movie ID to search for
     * @param genre Genre to search for
     * @param model Spring model for the HTML response
     * @return The movies HTML template with search results
     */
    @GetMapping("/movies/search/form")
    public String searchMoviesForm(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("HTML form search request - name: '{}', id: {}, genre: '{}'", name, id, genre);
        
        try {
            // Check if we have valid search parameters
            if (!movieService.hasValidSearchParameters(name, id, genre)) {
                logger.warn("No valid search parameters in form submission");
                model.addAttribute("movies", movieService.getAllMovies());
                model.addAttribute("searchError", "Arrr! Ye need to provide at least one search parameter, matey!");
                model.addAttribute("searchName", name);
                model.addAttribute("searchId", id);
                model.addAttribute("searchGenre", genre);
                return "movies";
            }
            
            // Perform the search
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            // Add results and search info to the model
            model.addAttribute("movies", searchResults);
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", id);
            model.addAttribute("searchGenre", genre);
            model.addAttribute("isSearchResult", true);
            model.addAttribute("totalResults", searchResults.size());
            
            if (searchResults.isEmpty()) {
                model.addAttribute("searchMessage", "Shiver me timbers! No movies found matching yer search criteria. Try different terms, ye scallywag!");
            } else {
                model.addAttribute("searchMessage", String.format("Yo ho ho! Found %d treasure%s matching yer search!", 
                    searchResults.size(), searchResults.size() == 1 ? "" : "s"));
            }
            
            logger.info("Form search completed! Found {} movies", searchResults.size());
            return "movies";
            
        } catch (Exception e) {
            logger.error("Error in form search: {}", e.getMessage(), e);
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("searchError", "Batten down the hatches! Something went wrong with the search. Try again later!");
            return "movies";
        }
    }
}