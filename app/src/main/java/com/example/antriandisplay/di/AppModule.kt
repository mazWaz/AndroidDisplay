package com.example.antriandisplay.di

import android.content.Context
import com.example.antriandisplay.App
import com.example.antriandisplay.BuildConfig
import com.example.antriandisplay.sockets.AppService
import com.example.antriandisplay.sockets.FlowStreamAdapter
import com.squareup.moshi.Moshi
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesApplication(@ApplicationContext context: Context): App {
        return context as App
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor()
            .setLevel(
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
            )

        return OkHttpClient.Builder()
            .pingInterval(5, TimeUnit.SECONDS)
            .addInterceptor(logger)
            .build()
    }

    @Singleton
    @Provides
    fun provideScarlet(application: App, client: OkHttpClient, moshi: Moshi): Scarlet {
        return Scarlet.Builder()
            .webSocketFactory(client.newWebSocketFactory("ws://192.168.137.39/ws"))
            .addMessageAdapterFactory(MoshiMessageAdapter.Factory(moshi))
            .addStreamAdapterFactory(FlowStreamAdapter.Factory())
            .backoffStrategy(ExponentialWithJitterBackoffStrategy(5000,500))
            .lifecycle(AndroidLifecycle.ofApplicationForeground(application))
            .build()
    }

    @Singleton
    @Provides
    fun provideAppService(scarlet: Scarlet): AppService {
        return scarlet.create()
    }
}