package tj.relax.data

import android.util.Log
import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow

private const val TAG = "UserRepository"

class UserRepository(
    private val api: RelaxApiService,
    private val localUserStore: LocalUserStore,
) {
    suspend fun getOrCreate(): UserProfile {
        val response = api.getProfile()
        if (!response.isSuccessful) {
            localUserStore.get()?.let {
                Log.w(TAG, "getOrCreate: failed code=${response.code}, using local profile")
                return it
            }
        }
        val profile = response.dataOrThrow()
        val result = profile.copy(level = computeLevel(profile.totalSpent))
        localUserStore.save(result)
        return result
    }

    suspend fun updateProfile(request: UpdateProfileRequest): UserProfile {
        val profile = api.updateProfile(request).dataOrThrow()
        val result = profile.copy(level = computeLevel(profile.totalSpent))
        localUserStore.save(result)
        return result
    }

    fun getCachedLocal(): UserProfile? = localUserStore.get()

    suspend fun uploadAvatar(bytes: ByteArray, mimeType: String = "image/jpeg"): UserProfile {
        val safeMimeType = mimeType.takeIf { it.startsWith("image/") } ?: "image/jpeg"
        val extension = when (safeMimeType) {
            "image/png"  -> "png"
            "image/webp" -> "webp"
            else         -> "jpg"
        }
        val profile = api.uploadAvatar(bytes, "avatar.$extension", safeMimeType).dataOrThrow()
        val result = profile.copy(level = computeLevel(profile.totalSpent))
        localUserStore.save(result)
        return result
    }

    suspend fun removeAvatar(): UserProfile {
        val profile = api.deleteAvatar().dataOrThrow()
        val result = profile.copy(level = computeLevel(profile.totalSpent))
        localUserStore.save(result)
        return result
    }

    fun clearCache() {
        localUserStore.clear()
    }

    private fun computeLevel(totalSpent: Double): LoyaltyLevel =
        MockData.loyaltyLevels.lastOrNull { it.minPoints <= totalSpent }
            ?: MockData.loyaltyLevels.first()
}
