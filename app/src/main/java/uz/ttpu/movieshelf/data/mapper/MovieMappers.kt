package uz.ttpu.movieshelf.data.mapper

import uz.ttpu.movieshelf.data.remote.MovieDto
import uz.ttpu.movieshelf.domain.model.Movie

// Why does the mapper belong to the data layer and not domain?
// Because the domain layer represents core business logic and must never know about 
// external data schemas or DTOs. The data layer adapts external data into domain entities.
fun MovieDto.toDomain(isFavorite: Boolean): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        year = this.releaseYear,
        rating = this.score,
        isFavorite = isFavorite
    )
}
