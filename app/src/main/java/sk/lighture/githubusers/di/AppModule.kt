package sk.lighture.githubusers.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import sk.lighture.githubusers.api.GithubApi
import sk.lighture.githubusers.ui.search.SearchRepository
import sk.lighture.githubusers.ui.search.SearchRepositoryImpl
import sk.lighture.githubusers.ui.user.UserRepository
import sk.lighture.githubusers.ui.user.UserRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    @Provides
    @Singleton
    fun provideJsonConverter(): Json = Json

    @ExperimentalSerializationApi
    @Provides
    @Singleton
    fun provideApi(json: Json, client: OkHttpClient): GithubApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        val api = retrofit.create(GithubApi::class.java)
        return api
    }

    @Provides
    @Singleton
    fun provideSearchRepository(
        api: GithubApi
    ): SearchRepository = SearchRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideUserRepository(
        api: GithubApi
    ): UserRepository = UserRepositoryImpl(api)

}