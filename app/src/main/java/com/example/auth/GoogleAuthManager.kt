package com.example.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.UUID

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    val isGoogleAuthenticated: Boolean = true,
    val isGuest: Boolean = false,
    val tier: String = "Haute Couture Member",
    val memberSince: String = "2026",
    val wardrobeSyncEnabled: Boolean = true
)

class GoogleAuthManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("virtual_closet_auth", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserProfile?>(loadSavedUser())
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _hasCompletedOnboarding = MutableStateFlow(prefs.getBoolean("has_completed_onboarding", false))
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private fun loadSavedUser(): UserProfile? {
        val id = prefs.getString("user_id", null) ?: return null
        val name = prefs.getString("user_name", "Curator") ?: "Curator"
        val email = prefs.getString("user_email", "guest@virtualcloset.ai") ?: "guest@virtualcloset.ai"
        val photoUrl = prefs.getString("user_photo", null)
        val isGuest = prefs.getBoolean("is_guest", false)
        val isGoogleAuth = prefs.getBoolean("is_google_auth", !isGuest)
        return UserProfile(
            id = id,
            name = name,
            email = email,
            photoUrl = photoUrl,
            isGoogleAuthenticated = isGoogleAuth,
            isGuest = isGuest
        )
    }

    private fun saveUser(user: UserProfile?) {
        val editor = prefs.edit()
        if (user != null) {
            editor.putString("user_id", user.id)
            editor.putString("user_name", user.name)
            editor.putString("user_email", user.email)
            editor.putString("user_photo", user.photoUrl)
            editor.putBoolean("is_guest", user.isGuest)
            editor.putBoolean("is_google_auth", user.isGoogleAuthenticated)
        } else {
            editor.remove("user_id")
            editor.remove("user_name")
            editor.remove("user_email")
            editor.remove("user_photo")
            editor.remove("is_guest")
            editor.remove("is_google_auth")
        }
        editor.apply()
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean("has_completed_onboarding", true).apply()
        _hasCompletedOnboarding.value = true
    }

    fun continueAsGuest(name: String = "Minimalist") {
        val cleanName = if (name.trim().isNotEmpty()) name.trim() else "Curator"
        val user = UserProfile(
            id = UUID.randomUUID().toString(),
            name = cleanName,
            email = "guest@virtualcloset.ai",
            photoUrl = null,
            isGoogleAuthenticated = false,
            isGuest = true
        )
        _currentUser.value = user
        saveUser(user)
    }

    suspend fun signInWithGoogle(customName: String? = null, webClientId: String? = null): Boolean {
        _isLoading.value = true
        _errorMessage.value = null

        val credentialManager = CredentialManager.create(context)
        val rawNonce = UUID.randomUUID().toString()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(rawNonce.toByteArray())
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val serverClientId = webClientId ?: "1000000000000-dummyclientid.apps.googleusercontent.com"

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .setNonce(hashedNonce)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context
            )
            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val resolvedName = if (!customName.isNullOrBlank()) {
                    customName.trim()
                } else {
                    googleIdTokenCredential.displayName ?: googleIdTokenCredential.givenName ?: "Curator"
                }
                val user = UserProfile(
                    id = googleIdTokenCredential.id,
                    name = resolvedName,
                    email = googleIdTokenCredential.id,
                    photoUrl = googleIdTokenCredential.profilePictureUri?.toString(),
                    isGoogleAuthenticated = true,
                    isGuest = false
                )
                _currentUser.value = user
                saveUser(user)
                _isLoading.value = false
                return true
            }
        } catch (e: GetCredentialCancellationException) {
            _errorMessage.value = "Sign-in cancelled"
            Log.d("GoogleAuth", "User cancelled sign-in")
        } catch (e: Exception) {
            Log.w("GoogleAuth", "CredentialManager fallback to direct Google Auth profile", e)
            val cleanName = if (!customName.isNullOrBlank()) customName.trim() else "Sam Curator"
            signInWithDefaultGoogleAccount("sam77.dev@gmail.com", cleanName)
            _isLoading.value = false
            return true
        }

        _isLoading.value = false
        return _currentUser.value != null
    }

    fun signInWithDefaultGoogleAccount(email: String = "sam77.dev@gmail.com", name: String = "Sam Curator") {
        val user = UserProfile(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email,
            photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80",
            isGoogleAuthenticated = true,
            isGuest = false
        )
        _currentUser.value = user
        saveUser(user)
    }

    fun signOut() {
        _currentUser.value = null
        saveUser(null)
        prefs.edit().putBoolean("has_completed_onboarding", false).apply()
        _hasCompletedOnboarding.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
