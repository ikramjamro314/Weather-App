package com.example.weatherapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun appNavigation() {

    val nv = rememberNavController()
    // if we initialize manualy viewModel then it is not going
    // to be recomposite over rotation or changes
    val vm: WeatherAppViewModel = viewModel()
    val state = vm.state.collectAsState()
    val weatherResult = vm.weatherResult.collectAsState()

    NavHost(navController = nv, startDestination = Routes.Signup.route) {
        composable(Routes.Signup.route) {
            SignUp(nv = nv , vm = vm, state = state.value, weatherResult = weatherResult.value)
        }
        composable(Routes.SignIn.route) {
            Login(nv = nv ,vm = vm, state = state.value, weatherResult = weatherResult.value)
        }
        composable(Routes.HomeScreen.route) {
            HomeScreen(vm = vm, state = state.value, weatherResult = weatherResult.value)
        }
    }
}