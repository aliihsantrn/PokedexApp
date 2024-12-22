package com.example.pokedexapp.di

import com.example.pokedexapp.data.remote.PokemonApi
import com.example.pokedexapp.repo.PokemonRepository
import com.example.pokedexapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePokemonRepository(api: PokemonApi): PokemonRepository {
        return PokemonRepository(api)
    }


    @Singleton
    @Provides
    fun provideApi() : PokemonApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .build()
            .create(PokemonApi::class.java)
    }
}