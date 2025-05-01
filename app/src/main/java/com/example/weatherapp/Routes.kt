package com.example.weatherapp

sealed class Routes(val route: String) {
        object Signup: Routes("sign_up")
        object SignIn: Routes("sign_in")
        object HomeScreen: Routes("home_screen")
    }