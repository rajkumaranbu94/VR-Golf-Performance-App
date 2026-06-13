# Architecture Overview

This document describes the high-level architecture of the Golf Performance App prototype.

Layers
- data
  - remote: `ApiService`, `MockInterceptor` (OkHttp) which serves JSON stored in `assets/`.
  - local: Room entities (`PlayerEntity`, `ShotEntity`), `PlayerDao`, `AppDatabase`.
- repository
  - `PlayerRepository`: single source of truth. Exposes Room Flows and provides `refreshFromRemote()` to fetch from the (mock) API and persist to Room.
- ui
  - Jetpack Compose screens: `PlayerListScreen`, `PlayerDetailScreen`.
  - `MainNavHost` provides navigation between screens.
- viewmodel
  - `PlayerViewModel` exposes players as a reactive `StateFlow` and supports a search query which filters players via a Room query.
- di
  - Koin module (`AppModule`) provides OkHttp, Retrofit, AppDatabase, DAO, Repository and registers `PlayerViewModel` via the Koin `viewModel` DSL.

Key decisions
- Offline-first: Room is the source for UI; repository writes remote data into Room on refresh.
- MockInterceptor: keeps networking realistic while being deterministic and offline-capable.
- Koin for DI: simple and lightweight for a prototype; ViewModel injected with Koin for Compose.

Files of interest
- `app/src/main/assets/mock_players.json` — mocked API data
- `app/src/main/java/com/golfperformance/app/data/local` — Room entities/DAO
- `app/src/main/java/com/golfperformance/app/data/remote` — ApiService and MockInterceptor
- `app/src/main/java/com/golfperformance/app/repository/PlayerRepository.kt`
- `app/src/main/java/com/golfperformance/app/viewmodel/PlayerViewModel.kt`
- `app/src/main/java/com/golfperformance/app/ui/players/PlayerListScreen.kt`

