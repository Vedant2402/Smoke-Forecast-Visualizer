## Smoke Forecast Visualizer 🌫️🔥

An Android app built with Kotlin and Jetpack Compose that displays real-time weather data and wildfire maps to help track smoke dispersion patterns.

## Contribution -
1. Vedant Kankate - Home Page, AQI Information & All files Integrations
2. Utkarsh Phatale - Smoke Visualizer
3. Harita Parikh - Weather API, Weather Search
4. darpan Patel - Weather Visualizer
5. Matthew graca - Database Preprocessing

## Features

- 🌡️ **Current Weather Data**: Temperature, "feels like" temperature, and precipitation
- 🔥 **Wildfire Heatmap**: Visualize active wildfires with a heatmap overlay
- ⏱️ **Hourly Forecast**: 12-hour temperature and rain predictions
- 📍 **Location-Based**: Automatically detects user location (or uses default coordinates)


## Screenshots

| Homepage | Weather Screen | Wildfire Map | Temperature Map | Weather Search |

![image](https://github.com/user-attachments/assets/b4635840-c03e-4355-b692-142400d6c6f0)

![image](https://github.com/user-attachments/assets/c9729d8d-0a56-4d8f-850e-28cd22348e9a)

![image](https://github.com/user-attachments/assets/7bbef6a2-6bef-4971-94b4-89086687a7b7)

![image](https://github.com/user-attachments/assets/0d5d25db-db51-4a43-ba64-07845039dd88)

![image](https://github.com/user-attachments/assets/5ec90f74-a80c-420b-99b1-f7043224ed09)

![temperature](https://github.com/user-attachments/assets/dc263547-29c0-489e-827d-6c5e432a090c)

## Technologies

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **Weather API** - Weather data source
- **NASA FIRMS API** - Wildfire data source
- **Google Maps SDK** - Map visualization
- **Retrofit** - API client
- **Coil** - Image loading

---

# Kotlin Functional Programming Concepts Used

### ✅ 1. Collections
**Used in:** `InformationActivity.kt`, `MapsActivity.kt`

**Examples:**
- `val levels = listOf(...)` and `val pollutants = listOf(...)` in `InformationActivity`.
- `val frameIds = (0..23).map { ... }` in `MapsActivity`.

---

### ✅ 2. Control Flow
**Used in:** All activities (`when`, `if`, `for`)

**Examples:**
- `when` in `MapsActivity.kt`: `getColorFromAQI(...)`
- `if (latRow == null || lonRow == null || densRow == null) continue` – classic `if/else` usage.

---

### ✅ 3. Lambda Expressions
**Used in:** `WeatherSearchActivity.kt`, `MapsActivity.kt`

**Examples:**
- `levels.forEach { level -> ... }` (lambda passed to `forEach`).
- Retrofit callbacks: `onResult: (Current) -> Unit`, passed as lambdas to `fetchWeather(...)`.

---

### ✅ 4. Classes / Data Classes
**Used in:** `WeatherSearchActivity.kt`

**Examples:**
- `data class Current(...)`, `AirQuality(...)`, `WeatherResponse(...)`.

These are plain Kotlin data classes with auto-generated `toString()`, `equals()`, `copy()`.

---

### ✅ 5. Safe Calls & Elvis Operator
**Used in:** `WeatherSearchActivity.kt`

**Examples:**
- `weather?.temp_c` (safe call).
- `current.air_quality.pm2_5 ?: "N/A"` (Elvis operator).

---

### ✅ 6. Generics
**Used in:** `Tasks.whenAllSuccess<DocumentSnapshot>(...)` in `MapsActivity.kt`

**Example:**
- This uses a generic method where `DocumentSnapshot` is the type parameter.

---

### ✅ 7. Exception Handling
**Used in:** `MainActivity.kt`, `WeatherSearchActivity.kt`

**Examples:**
- `try { ... } catch (e: Exception)` blocks wrap `Intent` and network calls.

---

### ✅ Bonus: MVC or MVVM Pattern
- Your app uses UI logic separation, particularly via ViewModel-like delegation (e.g., `WeatherSearchActivity` calling `fetchWeather` with callbacks).
- Your Composable UI code is mostly decoupled from business logic, which is clean MVVM-style separation.

---
# Perform After Installation.
## API Configuration

1. Create `secrets.properties` in your project root
2. Add these keys (get them from the respective services):
```properties
MAPS_API_KEY = Google API###
WEATHER_API_KEY = weather API###
