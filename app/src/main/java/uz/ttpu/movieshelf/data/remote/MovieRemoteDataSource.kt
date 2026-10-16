package uz.ttpu.movieshelf.data.remote

data class MovieDto(
    val id: Int,
    val title: String,
    val releaseYear: Int,
    val score: Double,
)

interface MovieRemoteDataSource {
    // Why suspend? Because network I/O takes time and must not block the main (UI) thread.
    suspend fun fetchMovies(): List<MovieDto>
}
