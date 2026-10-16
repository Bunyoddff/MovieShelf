package uz.ttpu.movieshelf.presentation.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ttpu.movieshelf.domain.model.Movie

@Composable
fun MovieListScreen(
    state: MovieListUiState,
    onFavoriteClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    onFavoritesOnlyChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is MovieListUiState.Loading -> {
                CircularProgressIndicator()
            }
            is MovieListUiState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onRefresh) {
                        Text("Retry")
                    }
                }
            }
            is MovieListUiState.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = state.favoritesOnly,
                            onClick = { onFavoritesOnlyChange(!state.favoritesOnly) },
                            label = { Text("Favorites only") }
                        )

                        TextButton(onClick = onRefresh) {
                            Text("Refresh")
                        }
                    }

                    if (state.isFromCache) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFE082))
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Offline — showing saved data",
                                color = Color(0xFF5D4037),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.movies, key = { it.id }) { movie ->
                            MovieItem(
                                movie = movie,
                                onFavoriteClick = { onFavoriteClick(movie.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(
    movie: Movie,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${movie.year} · Rating ${movie.rating}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (movie.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (movie.isFavorite) "Favorite" else "Not favorite",
                    tint = if (movie.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// 4 Previews
@Preview(showBackground = true)
@Composable
fun PreviewLoading() {
    MovieListScreen(state = MovieListUiState.Loading, onFavoriteClick = {}, onRefresh = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewError() {
    MovieListScreen(
        state = MovieListUiState.Error("No internet connection"),
        onFavoriteClick = {},
        onRefresh = {}
    )
}

val sampleMovies = listOf(
    Movie(1, "Echoes of Khiva", 2022, 9.0, isFavorite = true),
    Movie(2, "Registan Rhapsody", 2018, 8.4, isFavorite = false),
)

@Preview(showBackground = true)
@Composable
fun PreviewSuccessOnline() {
    MovieListScreen(
        state = MovieListUiState.Success(movies = sampleMovies, isFromCache = false),
        onFavoriteClick = {},
        onRefresh = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSuccessCached() {
    MovieListScreen(
        state = MovieListUiState.Success(movies = sampleMovies, isFromCache = true),
        onFavoriteClick = {},
        onRefresh = {}
    )
}
