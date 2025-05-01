package com.example.weatherapp

import WeatherApiData
import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HomeScreen(
    vm: WeatherAppViewModel,
    state: AppState,
    weatherResult: NetworkResponse<WeatherApiData>
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .padding(horizontal = 6.dp)
                    .background(Color(0xffFDFCFC), shape = RoundedCornerShape(15.dp))

            ) {
                TextField(
                    value = state.city.value,
                    onValueChange = { state.city.value = it },
                    maxLines = 1,
                    placeholder = {
                        Text(
                            "Search Location",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(6.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedPlaceholderColor = Color(0xffC4C4C4),
                        unfocusedPlaceholderColor = Color(0xffC4C4C4),
                    )
                )
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = Color(0xffC4C4C4),
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterVertically)
                )
            }

            Button(
                onClick = { vm.getCurrentWeather(city = state.city.value) },
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 65.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff3C6FD1),
                )
            ) { Text("Search") }

         Crossfade(
                targetState = weatherResult,
                animationSpec = tween(durationMillis = 300),
                label = "WeatherTransition"
            ) { state ->
                when (state) {
                    is NetworkResponse.Success ->
                        UiDetails(data = state.data)

                    is NetworkResponse.Error ->
                        Row( modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                state.message,
                                fontSize = 22.sp,
                                color = Color(0xff363B64),
                                fontWeight = FontWeight.Bold,
                            )
                        }

                    is NetworkResponse.Loading ->

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Loading()
                            Text(
                                "Hold for a Sec!",
                                fontSize = 22.sp,
                                color = Color(0xff363B64),
                                fontWeight = FontWeight.Bold
                            )
                        }

                    null -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Please search for a Location",
                                fontSize = 20.sp,
                                color = Color(0xff363B64),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
    }
    }


@Composable
fun UiDetails(data: WeatherApiData) {
    Column {
        AsyncImage(
            model = "https:${data.current.condition.icon}".replace(
                "64x64",
                "128x128"
            ),
            contentDescription = "",
            modifier = Modifier
                .size(170.dp)
                .align(Alignment.CenterHorizontally)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(
                text = data.location.name,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            AsyncImage(
                model = R.drawable.img,
                contentDescription = "",
                modifier = Modifier
                    .padding(8.dp)
                    .padding(horizontal = 5.dp)
            )
        }

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                data.current.temp_c.toString(),
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold
            )
            AsyncImage(
                model = R.drawable.degree,
                contentDescription = "",
                modifier = Modifier
                    .padding(8.dp)
                    .padding(bottom = 20.dp)
            )
        }

        val detailItems = listOf<itemDetails>(
            itemDetails(
                data.current.temp_f.toString() + "°",
                "Fahrenheit",
                R.drawable.thermo
            ),
            itemDetails(
                data.current.wind_mph.toString(),
                "Pressure",
                R.drawable.pressure
            ),
            itemDetails(
                data.current.uv.toString(),
                "UV Index",
                R.drawable.uv
            ),
            itemDetails(
                data.current.humidity.toString() + "%",
                "Humidity",
                R.drawable.humidity
            )
        )

        Text(
            "Details",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xff363B64),
            modifier = Modifier.padding(start = 20.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(10.dp).height(180.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false // Let LazyColumn handle scrolling
        ) {
            items(detailItems) {
                detailCard(it)
            }
        }
        Text(
            "Tips",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xff363B64),
            modifier = Modifier.padding(start = 20.dp)
        )
        Row(
            Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(16.dp)
                .background(Color(0xff3C6FD1), RoundedCornerShape(13.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "✨  Its ${data.current.condition.text} today!",
                fontSize = 17.sp,
                color = Color.White
            )
        }
    }
}

data class itemDetails(
    val detail: String,
    val text: String,
    val img: Int
)

@Composable
fun detailCard(details: itemDetails) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xffFDFCFC)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(76.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0xFF3D7BFF), // your desired shadow color
                spotColor = Color(0xFF3D7BFF)     // some devices use spotColor too
            )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = details.img,
                contentDescription = "",
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .size(40.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = details.detail,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = details.text,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xffA098AE)
                )
            }
        }
    }
}