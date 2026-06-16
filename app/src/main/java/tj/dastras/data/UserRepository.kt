package tj.dastras.data

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import tj.dastras.data.remote.RelaxApiService
import tj.dastras.data.remote.dataOrThrow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "UserRepository"

@Singleton
class UserRepository @Inject constructor(
    private val api: RelaxApiService,
    private val localUserStore: LocalUserStore,
) {
    @Volatile private var cached: UserProfile? = null

    suspend fun getOrCreate(forceRefresh: Boolean = false): UserProfile {
        cached?.let { if (!forceRefresh) return it }
        val response = api.getProfile()
        if (!response.isSuccessful) {
            localUserStore.get()?.let {
                Log.w(TAG, "getOrCreate: failed code=${response.code()}, using cached profile")
                return it
            }
        }
        val profile = response.dataOrThrow()
        val result = profile.copy(level = computeLevel(profile.totalSpent))
        cached = result
        localUserStore.save(result)
        return result
    }

    suspend fun updateProfile(request: UpdateProfileRequest): UserProfile {
        val profile = api.updateProfile(request).dataOrThrow()
        val result = profile.copy(level = computeLevel(profile.totalSpent))
        cached = result
        localUserStore.save(result)
        return result
    }

    /** Returns the locally cached profile (if any) without making a network request. */
    fun getCachedLocal(): UserProfile? = cached ?: localUserStore.get()

    /** Uploads the avatar image bytes via the backend and returns the updated profile. */
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
        cached = result
        localUserStore.save(result)
        return result
    }

    /** Removes the avatar via the backend and returns the updated profile. */
    suspend fun removeAvatar(): UserProfile {
        val profile = api.deleteAvatar().dataOrThrow()
        val result = profile.copy(level = computeLevel(profile.totalSpent))
        cached = result
        localUserStore.save(result)
        return result
    }

    fun clearCache() {
        cached = null
        localUserStore.clear()
    }

    private fun computeLevel(totalSpent: Double): LoyaltyLevel =
        MockData.loyaltyLevels.lastOrNull { it.minPoints <= totalSpent }
            ?: MockData.loyaltyLevels.first()
}
