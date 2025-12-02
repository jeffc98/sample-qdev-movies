# Movie Service - Spring Boot Demo Application 🏴‍☠️

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a pirate-themed search feature!

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **🏴‍☠️ Pirate Movie Search**: Ahoy matey! Search through our treasure chest of movies by name, ID, or genre with pirate flair!
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **REST API**: JSON API endpoints for developers to integrate with

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**
- **Thymeleaf** for templating

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **Search Movies**: Use the search form on the movies page or API endpoints below

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller for movie endpoints
│   │       │   ├── MovieService.java         # Business logic for movie operations
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       └── templates/
│           ├── movies.html                   # Movie list with search form
│           └── movie-details.html            # Movie details page
└── test/                                     # Unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/movies/
            ├── MovieServiceTest.java         # Service layer tests
            ├── MoviesControllerTest.java     # Controller tests
            └── MovieTest.java                # Model tests
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings, basic information, and a pirate-themed search form.

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

### 🏴‍☠️ Search Movies (JSON API)
```
GET /movies/search
```
Ahoy! Search through our treasure chest of movies using various criteria. Returns JSON response perfect for API integration.

**Query Parameters:**
- `name` (optional): Movie name to search for (case-insensitive partial matching)
- `id` (optional): Exact movie ID to find
- `genre` (optional): Genre to filter by (case-insensitive partial matching)

**Examples:**
```bash
# Search by movie name
curl "http://localhost:8080/movies/search?name=Prison"

# Search by genre
curl "http://localhost:8080/movies/search?genre=Drama"

# Search by ID
curl "http://localhost:8080/movies/search?id=1"

# Combine multiple criteria
curl "http://localhost:8080/movies/search?name=The&genre=Drama"
```

**Response Format:**
```json
{
  "success": true,
  "totalResults": 1,
  "message": "Yo ho ho! Found 1 treasure matching yer search!",
  "results": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years...",
      "duration": 142,
      "imdbRating": 5.0,
      "icon": "🎬"
    }
  ],
  "searchParameters": {
    "name": "Prison",
    "id": null,
    "genre": null
  }
}
```

### Search Movies (HTML Form)
```
GET /movies/search/form
```
Handles HTML form submissions from the search form on the movies page. Returns the movies page with filtered results.

**Query Parameters:** Same as JSON API above

**Example:**
```
http://localhost:8080/movies/search/form?name=Action&genre=Action
```

## Search Features 🔍

### Pirate-Themed Interface
- **Search Form**: Beautifully styled search form with pirate terminology
- **Error Messages**: Fun pirate-themed error messages like "Shiver me timbers!"
- **Success Messages**: Celebratory messages like "Yo ho ho! Found treasures!"

### Search Capabilities
- **Case-Insensitive**: All text searches are case-insensitive
- **Partial Matching**: Search for partial movie names or genres
- **Multiple Criteria**: Combine name, ID, and genre filters
- **Input Validation**: Proper validation with helpful error messages
- **Empty Results Handling**: Friendly messages when no movies are found

### Edge Cases Handled
- Empty or null search parameters
- Invalid movie IDs (negative or zero)
- Whitespace-only input
- Non-existent movies or genres
- Server errors with graceful fallback

## Testing

Run the comprehensive test suite:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MovieServiceTest

# Run with coverage
mvn test jacoco:report
```

### Test Coverage
- **MovieService**: Complete coverage of all search methods
- **MoviesController**: Tests for both JSON API and HTML form endpoints
- **Edge Cases**: Comprehensive testing of error conditions and edge cases
- **Integration**: Tests verify end-to-end functionality

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Check that at least one search parameter is provided
2. Verify movie data is loaded correctly in logs
3. Test with simple searches first (e.g., single character)

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog (`src/main/resources/movies.json`)
- Enhance the UI/UX with more pirate themes
- Add new search features like date ranges or rating filters
- Improve the responsive design
- Add more comprehensive error handling

### Adding New Movies

Edit `src/main/resources/movies.json` and add new movie objects:

```json
{
  "id": 13,
  "movieName": "New Movie Title",
  "director": "Director Name",
  "year": 2024,
  "genre": "Genre",
  "description": "Movie description...",
  "duration": 120,
  "imdbRating": 4.5
}
```

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May fair winds fill yer sails as ye explore this treasure chest of movies! 🏴‍☠️⚓*
