package uz.ttpu.movieshelf.domain.usecase

import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository

// Dependency Rule: The domain layer depends only on its own abstractions (MovieRepository interface).
// It does NOT import or depend on the data layer implementation (MovieRepositoryImpl).
class GetMoviesUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): MoviesResult {
        val result = repository.getMovies()
        // Sort rule: Rating descending, then Title ascending
        val sortedMovies = result.movies.sortedWith(
            compareByDescending<Movie> { it.rating }
                .thenBy { it.title }
        )
        return result.copy(movies = sortedMovies)
    }
}
