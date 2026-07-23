package tj.relax.core.di

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tj.relax.BuildConfig
import tj.relax.core.Interceptor.AuthInterceptor
import tj.relax.core.Interceptor.LanguageInterceptor
import tj.relax.core.Token.TokenAuthenticator
import tj.relax.core.api.ApiConfig
import tj.relax.core.api.RelaxApiService
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { AuthInterceptor(get()) }
    single { LanguageInterceptor() }
    single { TokenAuthenticator(get(), get()) }

    single<HttpLoggingInterceptor> {
        HttpLoggingInterceptor { message -> Log.d("OkHttp", message) }.apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
        }
    }

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(get<LanguageInterceptor>())
            .authenticator(get<TokenAuthenticator>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<RelaxApiService> { get<Retrofit>().create(RelaxApiService::class.java) }
}
