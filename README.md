# MovieShelf 🎬

An Android application demonstrating **Clean Architecture**, the **Dependency Rule**, and **Dependency Inversion** using Jetpack Compose and Material 3.

---

## 🏛 Architecture Overview

The project is structured strictly into three Clean Architecture layers:

```
uz.ttpu.movieshelf
├── presentation          # UI & State Holders
│   └── movies            # MovieListScreen (Jetpack Compose), MovieListViewModel (StateFlow)
├── domain                # Core Business Logic (Framework-independent)
│   ├── model             # Movie, MoviesResult entities
│   ├── repository        # MovieRepository (Interface - Dependency Inversion)
│   └── usecase           # GetMoviesUseCase, ToggleFavoriteUseCase, GetFavoriteMoviesUseCase
├── data                  # Data Management & Sources
│   ├── remote            # MovieRemoteDataSource, FakeMovieRemoteDataSource
│   ├── local             # MovieLocalDataSource, SharedPrefsMovieLocalDataSource
│   ├── mapper            # MovieMappers (DTO -> Domain Model)
│   └── repository        # MovieRepositoryImpl (Implements domain interface)
└── di                    # Manual Dependency Injection (AppContainer)
```

---

## ✨ Features

- **Unidirectional Data Flow (UDF):** Stateless UI components driven by sealed `MovieListUiState`.
- **Dependency Inversion:** Use cases depend on the domain `MovieRepository` interface; the data layer implements it.
- **Offline Caching:** Fallback to cached data when the network fails, with an "Offline — showing saved data" banner.
- **Persistent Favorites:** Local persistence using `SharedPreferences`.
- **Favorites Filter:** Quick toggle via `FilterChip` to filter favorites in real-time.
- **Unit Testing:** Fast unit tests with stubs and fakes for Use Cases and Repository without emulator dependencies.

---

## 🛠 Tech Stack

- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose (Material 3)
- **Architecture:** Clean Architecture + MVVM + Coroutines / StateFlow
- **Testing:** JUnit 4, Kotlinx Coroutines Test
