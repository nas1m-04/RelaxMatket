package tj.dastras.data

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import tj.dastras.data.remote.RelaxApiService
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
            Log.w(TAG, "getOrCreate: failed code=${response.code()}")
            localUserStore.get()?.let { return it }
        }
        val profile = response.body()?.data ?: UserProfile()
        val result = profile.copy(level = computeLevel(profile.bonusBalance))
        cached = result
        localUserStore.save(result)
        return result
    }

    suspend fun updateProfile(request: UpdateProfileRequest): UserProfile {
        val response = api.updateProfile(request)
        if (!response.isSuccessful) Log.w(TAG, "updateProfile: failed code=${response.code()}")
        val profile = response.body()?.data ?: UserProfile()
        val result = profile.copy(level = computeLevel(profile.bonusBalance))
        cached = result
        localUserStore.save(result)
        return result
    }

    /** Returns the locally cached profile (if any) without making a network request. */
    fun getCachedLocal(): UserProfile? = cached ?: localUserStore.get()

    /** Uploads the avatar image bytes via the backend and returns the updated profile. */
    suspend fun uploadAvatar(bytes: ByteArray): UserProfile {
        val body = bytes.toRequestBody("image/jpeg".toMediaType())
        val part = MultipartBody.Part.createFormData("avatar", "avatar.jpg", body)
        val response = api.uploadAvatar(part)
        if (!response.isSuccessful) Log.w(TAG, "uploadAvatar: failed code=${response.code()}")
        val profile = response.body()?.data ?: UserProfile()
        val result = profile.copy(level = computeLevel(profile.bonusBalance))
        cached = result
        localUserStore.save(result)
        return result
    }

    /** Removes the avatar via the backend and returns the updated profile. */
    suspend fun removeAvatar(): UserProfile {
        val response = api.deleteAvatar()
        if (!response.isSuccessful) Log.w(TAG, "removeAvatar: failed code=${response.code()}")
        val profile = response.body()?.data ?: UserProfile()
        val result = profile.copy(level = computeLevel(profile.bonusBalance))
        cached = result
        localUserStore.save(result)
        return result
    }

    fun clearCache() {
        cached = null
        localUserStore.clear()
    }

    private fun computeLevel(bonusBalance: Double): LoyaltyLevel =
        MockData.loyaltyLevels.lastOrNull { it.minPoints <= bonusBalance }
            ?: MockData.loyaltyLevels.first()
}
