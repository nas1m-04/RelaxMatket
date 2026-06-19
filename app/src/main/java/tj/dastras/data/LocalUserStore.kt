package tj.dastras.data

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Caches the user's profile locally so it's available instantly on app start, until logout. */
@Singleton
class LocalUserStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson  = Gson()

    fun save(profile: UserProfile) {
        prefs.edit { putString(KEY_PROFILE, gson.toJson(profile)) }
    }

    fun get(): UserProfile? =
        prefs.getString(KEY_PROFILE, null)?.let {
            try { gson.fromJson(it, UserProfile::class.java) } catch (e: Exception) { null }
        }

    fun clear() {
        prefs.edit { remove(KEY_PROFILE) }
    }

    fun saveDeliveryAddress(address: String) {
        prefs.edit { putString(KEY_DELIVERY_ADDRESS, address) }
    }

    fun getDeliveryAddress(): String = prefs.getString(KEY_DELIVERY_ADDRESS, "") ?: ""

    companion object {
        private const val KEY_PROFILE          = "profile"
        private const val KEY_DELIVERY_ADDRESS = "delivery_address"
    }
}
