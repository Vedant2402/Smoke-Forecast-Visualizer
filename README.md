## Smoke Forecast Visualizer 🌫️🔥

An Android app built with Kotlin and Jetpack Compose that displays real-time weather data and wildfire maps to help track smoke dispersion patterns.

## Features

- 🌡️ **Current Weather Data**: Temperature, "feels like" temperature, and precipitation
- 🔥 **Wildfire Heatmap**: Visualize active wildfires with a heatmap overlay
- ⏱️ **Hourly Forecast**: 12-hour temperature and rain predictions
- 📍 **Location-Based**: Automatically detects user location (or uses default coordinates)


## Screenshots

| Weather Screen | Wildfire Map |

![image](https://github.com/user-attachments/assets/4f0b66a1-3fb5-4866-a4f1-cbd640680d11)

![image](https://github.com/user-attachments/assets/c9729d8d-0a56-4d8f-850e-28cd22348e9a)

![image](https://github.com/user-attachments/assets/7bbef6a2-6bef-4971-94b4-89086687a7b7)

![image](https://github.com/user-attachments/assets/0d5d25db-db51-4a43-ba64-07845039dd88)

![image](https://github.com/user-attachments/assets/5ec90f74-a80c-420b-99b1-f7043224ed09)


## Technologies

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **Open-Meteo API** - Weather data source
- **NASA FIRMS API** - Wildfire data source
- **Google Maps SDK** - Map visualization
- **Retrofit** - API client
- **Coil** - Image loading

## API Configuration

1. Create `secrets.properties` in your project root
2. Add these keys (get them from the respective services):
```properties
MAPS_API_KEY = Google API###
WEATHER_API_KEY = weather API###
