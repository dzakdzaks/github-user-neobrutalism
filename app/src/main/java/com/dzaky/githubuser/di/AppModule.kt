package com.dzaky.githubuser.di

import com.dzaky.githubuser.BuildConfig
import com.dzaky.githubuser.common.IoDispatcher
import com.dzaky.githubuser.data.remote.GitHubApi
import com.dzaky.githubuser.data.repository.GitHubRepositoryImpl
import com.dzaky.githubuser.domain.repository.GitHubRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @IoDispatcher
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideHttpClientBuilder(): OkHttpClient.Builder {
        val loggerInterceptor = HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG)
                setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .addInterceptor(Interceptor { chain ->
                val request: Request = chain.request()
                    .newBuilder()
                    .header("Authorization", BuildConfig.GITHUB_TOKEN)
                    .build()
                chain.proceed(request)
            })
            .addInterceptor(loggerInterceptor)
    }

    @Provides
    @Singleton
    fun provideGitHubApi(httpClientBuilder: OkHttpClient.Builder): GitHubApi {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(httpClientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGitHubRepository(api: GitHubApi): GitHubRepository {
        return GitHubRepositoryImpl(api)
    }
}
