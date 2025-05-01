package com.example.weatherapp

import WeatherApiData
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class WeatherAppViewModel : ViewModel() {

    private val _state = MutableStateFlow<AppState>(AppState())
    val state: StateFlow<AppState> = _state

    private val _weatherResult =
        MutableStateFlow<NetworkResponse<WeatherApiData>>(NetworkResponse.Error("Please Search for a Location!"))
    val weatherResult: StateFlow<NetworkResponse<WeatherApiData>> = _weatherResult

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    init {
        if (auth.currentUser != null) {
            _state.value.isSignIn.value = true
        }
    }


    fun getCurrentWeather(city: String) {
        if (city.isNotEmpty()) {
            _weatherResult.value = NetworkResponse.Loading
            viewModelScope.launch {
                try {
                    val response = getApiServices().getCurrentWeather(KEY, city)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _weatherResult.value = NetworkResponse.Success(it)
                        }
                    } else {
                        _weatherResult.value =
                            NetworkResponse.Error("Failed to Load Data, Enter a Valid Location!")
                    }
                } catch (e: Exception) {
                    _weatherResult.value =
                        NetworkResponse.Error("Failed to Load Data, Enter a Valid Location!")
                }
            }
        } else {
            _weatherResult.value =
                NetworkResponse.Error("Enter a Valid Location!")
        }
    }

    fun logIn(context: Context , nv: NavController){
        val email = _state.value.email.value
        val password = _state.value.password.value

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill all the fields!", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(
                context,
                "Password should be at least 6 characters!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!email.contains("@gmail.com")) {
            Toast.makeText(context, "Please enter a valid email!", Toast.LENGTH_SHORT).show()
            return
        }

        _weatherResult.value = NetworkResponse.Loading

        auth.signInWithEmailAndPassword(email , password).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context , "Login Successfully" , Toast.LENGTH_SHORT).show()
                nv.navigate(Routes.HomeScreen.route){
                    popUpTo(0) {inclusive = true}
                }

                _state.value.email.value=""
                _state.value.password.value=""
                _state.value.isChecked.value=false

                _weatherResult.value = NetworkResponse.Error("")
            }else{
                Toast.makeText(context , "Login Failed!" , Toast.LENGTH_SHORT).show()
                _weatherResult.value = NetworkResponse.Error("")
            }
        }.addOnFailureListener {
            Toast.makeText(context , "Login Failed!" , Toast.LENGTH_SHORT).show()
            _weatherResult.value = NetworkResponse.Error("")
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun signUp(context: Context , nv: NavController) {
        val name = _state.value.name.value
        val email = _state.value.email.value
        val password = _state.value.password.value

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill all the fields!", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(
                context,
                "Password should be at least 6 characters!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!email.contains("@gmail.com")) {
            Toast.makeText(context, "Please enter a valid email!", Toast.LENGTH_SHORT).show()
            return
        }

        if(!_state.value.isChecked.value){
            Toast.makeText(context, "Please accept the terms and conditions!", Toast.LENGTH_SHORT).show()
            return
        }

        _weatherResult.value = NetworkResponse.Loading

        db.collection("users").whereEqualTo("email", email).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = hashMapOf(
                            "name" to name,
                            "email" to email
                        )
                        db.collection("users").document(email).set(user).addOnFailureListener {
                            Toast.makeText(context, "User data failed!!", Toast.LENGTH_SHORT)
                                .show()
                        }
                        _weatherResult.value = NetworkResponse.Error("")
                        Toast.makeText(context, "Sign up successfully", Toast.LENGTH_SHORT).show()
                        nv.navigate(Routes.SignIn.route){
                           popUpTo(0){inclusive = true}
                       }

                        _state.value.name.value=""
                        _state.value.email.value=""
                        _state.value.password.value=""
                        _state.value.isChecked.value=false
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "Sign up failed!!", Toast.LENGTH_SHORT).show()
                    _weatherResult.value = NetworkResponse.Error("")
                }
            } else {
                Toast.makeText(context, "User already exists!!", Toast.LENGTH_SHORT).show()
                _weatherResult.value = NetworkResponse.Error("")
            }

        }.addOnFailureListener {
            Toast.makeText(context, "User already exists!!", Toast.LENGTH_SHORT).show()
            _weatherResult.value = NetworkResponse.Error("")
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun SignInWithGoogle(
        context: Context,
        scope: CoroutineScope,
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>? = null,
        result: Intent?
    ) {
        val credential = CredentialManager.create(context)
        _weatherResult.value = NetworkResponse.Loading
        try {
        scope.launch {


            if (result != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result)
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                val user = authResult.user
                user?.let {
                    if (it.isAnonymous.not()) {
                        val user = hashMapOf(
                            "name" to it.displayName,
                            "email" to it.email
                        )
                        db.collection("users").document(it.email.toString()).set(user)
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "User data failed!!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        _weatherResult.value = NetworkResponse.Error("")
                        Toast.makeText(context, "Sign up successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }


            val result = credential.getCredential(context, request = GoogleSignInstance.request)
            when (result.credential) {
                is CustomCredential -> {
                    if (result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(result.credential.data)
                        val googleTokenId = googleIdTokenCredential.idToken
                        val authCredential =
                            GoogleAuthProvider.getCredential(googleTokenId, null)
                        val user = auth.signInWithCredential(authCredential).await().user
                        user?.let {
                            if (it.isAnonymous.not()) {
                                val user = hashMapOf(
                                    "name" to it.displayName,
                                    "email" to it.email
                                )
                                db.collection("users").document(it.email.toString()).set(user)
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "User data failed!!",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                _weatherResult.value = NetworkResponse.Error("")
                                Toast.makeText(context, "Sign up successfully", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                    }
                }

                else -> {
                    Toast.makeText(context, "Sign in failed", Toast.LENGTH_SHORT).show()
                    _weatherResult.value = NetworkResponse.Error("")
                }
            }
        }
            } catch (e: NoCredentialException) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.Web_ID)) // Use getString to get the string value
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            val signInIntent = googleSignInClient.signInIntent


                launcher?.launch(signInIntent)
                _weatherResult.value = NetworkResponse.Error("")
            } catch (e: GetCredentialException) {
                Toast.makeText(context, "Sign in failed", Toast.LENGTH_SHORT).show()
                _weatherResult.value = NetworkResponse.Error("")
            } catch (e: TimeoutCancellationException) {
                Toast.makeText(context, "Sign in timed out. Try again.", Toast.LENGTH_SHORT).show()
                _weatherResult.value = NetworkResponse.Error("")
            } catch (e: Exception) {
                // Generic exception catch-all
                Toast.makeText(
                    context,
                    "Unexpected error: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
                _weatherResult.value = NetworkResponse.Error("")
            }
        }
    }


data class AppState(
    var city: MutableState<String> = mutableStateOf(""),
    var name: MutableState<String> = mutableStateOf(""),
    var email: MutableState<String> = mutableStateOf(""),
    var password: MutableState<String> = mutableStateOf(""),
    var isChecked: MutableState<Boolean> = mutableStateOf(false),
    var isSignIn: MutableState<Boolean> = mutableStateOf(false),
    var isPasswordVisible: MutableState<Boolean> = mutableStateOf(false)
)