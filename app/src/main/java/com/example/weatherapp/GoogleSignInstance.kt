package com.example.weatherapp

import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption

object GoogleSignInstance {
    // Instantiate a Google sign-in request
    val googleIdOption = GetGoogleIdOption.Builder()
        // Only show accounts previously used to sign in.
        .setFilterByAuthorizedAccounts(false)
        .setAutoSelectEnabled(false)
        // Your server's client ID, not your Android client ID.
        .setServerClientId(R.string.Web_ID.toString())
        .build()

    // Create the Credential Manager request
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
}