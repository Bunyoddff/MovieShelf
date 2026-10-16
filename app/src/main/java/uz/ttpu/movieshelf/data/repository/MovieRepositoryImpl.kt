package uz.ttpu.movieshelf.data.repository

import uz.ttpu.movieshelf.data.local.MovieLocalDataSource
import uz.ttpu.movieshelf.data.mapper.toDomain
import uz.ttpu.movieshelf.data.remote.MovieRemoteDataSource
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class MovieRepositoryImpl(
    private val remote: MovieRemoteDataSource,
    private val local: MovieLocalDataSource,
) : MovieRepository {

    override suspend fun getMovies(): MoviesResult {
        val favoriteIds = local.getFavoriteIds()
        return try {
            val remoteDtos = remote.fetchMovies()
            local.saveMovies(remoteDtos)
            val domainMovies = remoteDtos.map { it.toDomain(isFavorite = it.id in favoriteIds) }
            MoviesResult(movies = domainMovies, isFromCache = false)
        } catch (e: CancellationException) {
            throw e // Never swallow coroutine cancellation!
        } catch (e: IOException) {
            val cachedDtos = local.getCachedMovies()
            if (cachedDtos != null) {
                val domainMovies = cachedDtos.map { it.toDomain(isFavorite = it.id in favoriteIds) }
                MoviesResult(movies = domainMovies, isFromCache = true)
            } else {
                throw e
            }
        }
    }

    override suspend fun toggleFavorite(movieId: Int): Boolean {
        val favorites = local.getFavoriteIds()
        val newFavoriteState = movieId !in favorites
        local.setFavorite(movieId, newFavoriteState)
        return newFavoriteState
    }
}
