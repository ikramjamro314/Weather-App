package com.example.weatherapp

import WeatherApiData
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Login(
    nv: NavController,
    state: AppState,
    vm: WeatherAppViewModel,
    weatherResult: NetworkResponse<WeatherApiData>
){

    LaunchedEffect(Unit) { // Run when isSignIn changes (or initially)
        if (state.isSignIn.value) {
            nv.navigate(Routes.HomeScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }
        state.isSignIn.value = false // Reset the flag
        }


    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val context = LocalContext.current

        if (weatherResult is NetworkResponse.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Loading()
            }
        }

        Text(
            text = "Sign in",
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xff2A4ECA)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            " Welcome to our Weather App\n" +
                    "Get accurate weather updates anytime, anywhere!",
            fontSize = 15.sp,
            color = Color(0xff61677D),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = state.email.value,
            onValueChange = { state.email.value = it },
            placeholder = { Text("Email", fontSize = 18.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                unfocusedContainerColor = Color(0xffF5F5F5),
                focusedContainerColor = Color(0xffF5F5F5),
                cursorColor = Color(0xff2A4ECA)
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 18.dp)
                .background(Color(0xffF5F5F5), shape = RoundedCornerShape(14.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            OutlinedTextField(
                value = state.password.value,
                onValueChange = { state.password.value = it },
                visualTransformation = if (state.isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                placeholder = { Text("Password", fontSize = 18.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color(0xff2A4ECA)
                )
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.eye),
                contentDescription = "Visibility",
                modifier = Modifier
                    .padding(end = 14.dp)
                    .clickable {
                        state.isPasswordVisible.value = !state.isPasswordVisible.value
                    }
            )
        }

        Spacer(Modifier.height(20.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
        ) {
            Divider(
                modifier = Modifier
                    .weight(1f)
                    .padding(9.dp)
            )
            Text("Or", fontSize = 16.sp)
            Divider(
                Modifier
                    .weight(1f)
                    .padding(9.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .background(Color(0xffF5F5F5))
                    .clickable {

                    },
                shape = RoundedCornerShape(14.dp)
            ) {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.fb),
                        contentDescription = "Facebook",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(30.dp)
                    )
                    Text(
                        "Facebook",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xff61677D),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .background(Color(0xffF5F5F5))
                    .clickable {
                        Toast.makeText(context, "Not Procession", Toast.LENGTH_LONG).show()
                    },
                shape = RoundedCornerShape(14.dp)
            ) {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.google),
                        contentDescription = "Google",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(30.dp)
                    )
                    Text(
                        "Google",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xff61677D),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                vm.logIn(context , nv = nv)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(18.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xff3461FD),
                contentColor = Color.White
            )
        ) {
            Text("Login", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        ) {
            Text("Do you have any account? ", fontSize = 17.sp, color = Color(0xff61677D))
            Text(
                "Sign Up", fontSize = 17.sp, color = Color(0xff3461FD),
                modifier = Modifier.clickable {
                 nv.navigate(Routes.Signup.route){
                     popUpTo(0) {inclusive = true}
                 }

                    state.name.value=""
                    state.email.value=""
                    state.password.value=""

                }

            )
        }
    }
}