package tj.relax.data

import com.russhwolf.settings.Settings
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Caches the user's profile locally so it's available instantly on app start, until logout. */
class LocalUserStore(
    private val settings: Settings,
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun save(profile: UserProfile) {
        settings.putString(KEY_PROFILE, json.encodeToString<UserProfile>(profile))
    }

    fun get(): UserProfile? =
        settings.getStringOrNull(KEY_PROFILE)?.let {
            try { json.decodeFromString<UserProfile>(it) } catch (e: Exception) { null }
        }

    fun clear() {
        settings.remove(KEY_PROFILE)
    }

    fun saveDeliveryAddress(address: String) {
        settings.putString(KEY_DELIVERY_ADDRESS, address)
    }

    fun getDeliveryAddress(): String = settings.getString(KEY_DELIVERY_ADDRESS, "")

    companion object {
        private const val KEY_PROFILE          = "profile"
        private const val KEY_DELIVERY_ADDRESS = "delivery_address"
    }
}
