package uz.ttpu.movieshelf.data.local

import android.content.Context
import uz.ttpu.movieshelf.data.remote.MovieDto

class SharedPrefsMovieLocalDataSource(context: Context) : MovieLocalDataSource {

    private val prefs = context.getSharedPreferences("movieshelf", Context.MODE_PRIVATE)
    private var cached: List<MovieDto>? = null

    override suspend fun getCachedMovies(): List<MovieDto>? = cached

    override suspend fun saveMovies(movies: List<MovieDto>) {
        cached = movies
    }

    override suspend fun getFavoriteIds(): Set<Int> {
        val stringSet = prefs.getStringSet("favorites", emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    override suspend fun setFavorite(id: Int, favorite: Boolean) {
        val currentFavorites = getFavoriteIds().map { it.toString() }.toMutableSet()
        if (favorite) {
            currentFavorites.add(id.toString())
        } else {
            currentFavorites.remove(id.toString())
        }
        prefs.edit().putStringSet("favorites", currentFavorites).apply()
    }
}
