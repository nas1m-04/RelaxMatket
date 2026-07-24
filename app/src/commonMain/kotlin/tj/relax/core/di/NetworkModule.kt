package tj.relax.core.di

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import tj.relax.core.Interceptor.LanguageInterceptor
import tj.relax.core.Token.TokenAuthenticator
import tj.relax.core.api.RelaxApiService
import tj.relax.core.util.isDebugBuild

val networkModule = module {
    single { TokenAuthenticator(get(), get()) }

    single<HttpClient> {
        val tokenAuthenticator = get<TokenAuthenticator>()
        HttpClient {
            expectSuccess = false

            install(HttpTimeout) {
                connectTimeoutMillis = 30_000
                requestTimeoutMillis = 30_000
                socketTimeoutMillis = 30_000
            }

            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }

            install(LanguageInterceptor)

            install(Auth) {
                bearer {
                    loadTokens { tokenAuthenticator.loadTokens() }
                    refreshTokens { tokenAuthenticator.refreshTokens(client) }
                    sendWithoutRequest { true }
                }
            }

            if (isDebugBuild) {
                install(Logging) {
                    level = LogLevel.BODY
                    logger = object : Logger {
                        override fun log(message: String) {
                            Napier.d(message, tag = "Ktor")
                        }
                    }
                }
            }
        }
    }

    single<RelaxApiService> { RelaxApiService(get()) }
}
