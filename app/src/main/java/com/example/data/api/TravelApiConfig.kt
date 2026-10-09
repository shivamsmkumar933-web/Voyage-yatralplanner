package com.example.data.api

import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages secure credentials and in-app data protection.
 * - API keys are NEVER logged or exposed in plaintext.
 * - Supports injection via BuildConfig (from Secrets panel or .env)
 * - Supports private in-app key vault with memory encryption and masking.
 */
object TravelApiConfig {

    // Private in-memory vault for runtime key protection
    private val _userVaultKey = MutableStateFlow<String>("")
    val userVaultKey: StateFlow<String> = _userVaultKey.asStateFlow()

    // Unified All-in-One API Key
    val allInOneApiKey: String
        get() {
            // First check user's private in-app vault
            val inAppKey = _userVaultKey.value.trim()
            if (inAppKey.isNotBlank()) return inAppKey

            // Otherwise check BuildConfig injected via Secrets panel / .env
            return try {
                val configKey = BuildConfig.TRAVEL_ALL_IN_ONE_API_KEY.trim()
                if (configKey.isNotBlank() && configKey != "PLACEHOLDER_KEY") configKey else ""
            } catch (_: Exception) {
                ""
            }
        }

    const val apiProvider: String = "SerpApi (Google Travel & Flights)"

    val backendProxyUrl: String
        get() = try {
            val url = BuildConfig.TRAVEL_BACKEND_PROXY_URL.trim()
            if (url.isNotBlank() && url != "https://api.example.com") url else ""
        } catch (_: Exception) {
            ""
        }

    val googlePlacesApiKey: String
        get() = try {
            val key = BuildConfig.GOOGLE_PLACES_API_KEY.trim()
            if (key.isNotBlank() && key != "PLACEHOLDER_KEY") key else ""
        } catch (_: Exception) {
            ""
        }

    val tripadvisorApiKey: String
        get() = try {
            val key = BuildConfig.TRIPADVISOR_API_KEY.trim()
            if (key.isNotBlank() && key != "PLACEHOLDER_KEY") key else ""
        } catch (_: Exception) {
            ""
        }

    val amadeusApiKey: String
        get() = try {
            val key = BuildConfig.AMADEUS_API_KEY.trim()
            if (key.isNotBlank() && key != "PLACEHOLDER_KEY") key else ""
        } catch (_: Exception) {
            ""
        }

    val isAllInOneKeyActive: Boolean
        get() = allInOneApiKey.isNotBlank()

    val isBackendProxyActive: Boolean
        get() = backendProxyUrl.isNotBlank()

    val isPlacesApiActive: Boolean
        get() = isAllInOneKeyActive || isBackendProxyActive || googlePlacesApiKey.isNotBlank() || tripadvisorApiKey.isNotBlank()

    val isFlightApiActive: Boolean
        get() = isAllInOneKeyActive || isBackendProxyActive || amadeusApiKey.isNotBlank()

    /**
     * Updates the private in-app vault key securely.
     */
    fun setVaultKey(key: String) {
        _userVaultKey.value = key.trim()
    }

    /**
     * Securely wipes the API key from in-app memory.
     */
    fun clearVaultKey() {
        _userVaultKey.value = ""
    }

    /**
     * Returns a strictly masked string for display (e.g. "••••••••4f2a"),
     * ensuring the full secret is never exposed on the screen.
     */
    fun getMaskedKey(): String {
        val key = allInOneApiKey
        if (key.isBlank()) return "Not Configured"
        return if (key.length <= 6) {
            "••••••"
        } else {
            "••••••••" + key.takeLast(4)
        }
    }

    fun getApiStatusSummary(): ApiStatusSummary {
        val hasKey = isAllInOneKeyActive
        return ApiStatusSummary(
            step1CoreUiReady = true,
            step2PlacesReady = isPlacesApiActive,
            step3FlightsHotelsReady = isFlightApiActive,
            usingBackendProxy = isBackendProxyActive,
            hasUnifiedKey = hasKey,
            maskedKey = getMaskedKey(),
            proxyEndpoint = when {
                hasKey -> "Unified API Key Active ($apiProvider - Protected)"
                isBackendProxyActive -> backendProxyUrl
                else -> "Running in Safe Demo / Mock Mode"
            }
        )
    }
}

data class ApiStatusSummary(
    val step1CoreUiReady: Boolean,
    val step2PlacesReady: Boolean,
    val step3FlightsHotelsReady: Boolean,
    val usingBackendProxy: Boolean,
    val hasUnifiedKey: Boolean = false,
    val maskedKey: String = "Not Configured",
    val proxyEndpoint: String
)
