package uz.ttpu.movieshelf.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.ToggleFavoriteUseCase
import java.io.IOException

/*
 * Task 15 — Trace Analysis:
 * 1. Order of calls when tapping a favorite heart:
 *    MovieItem (UI onClick) -> MovieListViewModel.onFavoriteClick -> ToggleFavoriteUseCase.invoke
 *    -> MovieRepositoryImpl.toggleFavorite -> SharedPrefsMovieLocalDataSource.setFavorite
 *
 * 2. Dependency arrows between these 5 classes:
 *    MovieItem -> MovieListViewModel -> ToggleFavoriteUseCase -> MovieRepository (interface in domain)
 *    <- MovieRepositoryImpl (data layer implements interface) -> SharedPrefsMovieLocalDataSource
 *    Notice: The arrow between ToggleFavoriteUseCase and MovieRepositoryImpl points OPPOSITE to the call
 *    direction (Dependency Inversion principle). The domain owns the interface; data depends inward on domain.
 *
 * 3. Logging in Domain:
 *    The domain layer is pure Kotlin and does not depend on Android SDK, so android.util.Log cannot be called.
 *    The clean architectural solution is declaring an interface (e.g. `interface Logger { fun d(tag: String, msg: String) }`)
 *    in the domain layer, and implementing it in the outer presentation or app layer (Dependency Inversion).
 */

sealed interface MovieListUiState {
    data object Loading : MovieListUiState
    data class Success(
        val movies: List<Movie>,
        val isFromCache: Boolean,
        val favoritesOnly: Boolean = false,
    ) : MovieListUiState
    data class Error(val message: String) : MovieListUiState
}

class MovieListViewModel(
    private val getMovies: GetMoviesUseCase,
    private val getFavoriteMovies: GetFavoriteMoviesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val state: StateFlow<MovieListUiState> = _state.asStateFlow()

    private var favoritesOnly: Boolean = false

    init {
        load()
    }

    fun onRefresh() = load()

    fun onFavoritesOnlyChange(enabled: Boolean) {
        favoritesOnly = enabled
        load()
    }

    fun onFavoriteClick(movieId: Int) {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState is MovieListUiState.Success) {
                val newFavorite = toggleFavorite(movieId)
                val updatedMovies = if (favoritesOnly && !newFavorite) {
                    currentState.movies.filter { it.id != movieId }
                } else {
                    currentState.movies.map { movie ->
                        if (movie.id == movieId) movie.copy(isFavorite = newFavorite) else movie
                    }
                }
                _state.value = currentState.copy(movies = updatedMovies)
            }
        }
    }

    private fun load() {
        _state.value = MovieListUiState.Loading
        viewModelScope.launch {
            try {
                val result = if (favoritesOnly) getFavoriteMovies() else getMovies()
                _state.value = MovieListUiState.Success(
                    movies = result.movies,
                    isFromCache = result.isFromCache,
                    favoritesOnly = favoritesOnly
                )
            } catch (e: IOException) {
                _state.value = MovieListUiState.Error("Can't load movies. Check your connection and try again.")
            }
        }
    }
}
