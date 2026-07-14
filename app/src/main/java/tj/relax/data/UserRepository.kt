package tj.relax.data

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "UserRepository"

@Singleton
class UserRepository @Inject constructor(
    private val api: RelaxApiService,
    private val localUserStore: LocalUserStore,
) {
    suspend fun getOrCreate(): UserProfile {
        val response = api.getProfile()
        if (!response.isSuccessful) {
            localUserStore.get()?.let {
                Log.w(TAG, "getOrCreate: failed code=${response.code()}, using local profile")
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
        val body = bytes.toRequestBody(safeMimeType.toMediaType())
        val part = MultipartBody.Part.createFormData("avatar", "avatar.$extension", body)
        val profile = api.uploadAvatar(part).dataOrThrow()
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
