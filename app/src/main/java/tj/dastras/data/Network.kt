package tj.dastras.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tj.dastras.BuildConfig
import java.util.concurrent.TimeUnit

/** Base URL of the RELAX backend. Replace with the real endpoint once it's available. */
private const val BASE_URL = "https://api.relax.tj/"

private val loggingInterceptor = HttpLoggingInterceptor().apply {
    // Logs full request/response lines, headers and bodies under the "OkHttp" tag in Logcat.
    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

val relaxRetrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
