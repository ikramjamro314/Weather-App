Weather App
📌 Overview
The Weather App provides real-time weather updates by fetching data from an external REST API. It displays current weather conditions such as temperature, humidity, wind speed, and more based on the user's selected location. Designed with Jetpack Compose, the app ensures a modern, responsive, and user-friendly interface.

💡 Idea Behind the Project
Weather data plays a vital role in our daily decision-making—whether it's planning travel, dressing appropriately, or scheduling outdoor events. I wanted to create a weather app that not only looks great but also provides reliable real-time information using modern Android development practices. This project allowed me to explore API integration, UI responsiveness, and state management using the latest Android tools.

🌟 Impact of the App
🌤️ Real-Time Weather Info – Instantly fetches current weather conditions from a global weather API.
📍 Location-Based Search – Users can search weather by city name or region.
📱 Clean & Intuitive UI – Built with Jetpack Compose for a smooth user experience.
🔁 Dynamic Data Update – Automatically updates weather details based on latest API responses.
⚡ Lightweight & Fast – Optimized network calls with Retrofit and Coroutines for minimal lag.

⚙️ How I Built It
The development of this app involved the following key components:

UI Design – Used Jetpack Compose to create a responsive and modern interface.

API Integration – Fetched live weather data from OpenWeatherMap (or other REST API) using Retrofit.

Asynchronous Operations – Implemented Kotlin Coroutines to handle network calls without blocking the UI.

State Management – Managed real-time UI updates using ViewModel and LiveData.

Error Handling – Handled API errors and connectivity issues gracefully with proper messaging.

Data Parsing – Parsed JSON responses into data classes for efficient data usage.

