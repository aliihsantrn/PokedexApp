package com.example.pokedexapp.data.models

import retrofit2.http.Url

data class PokemonListModel(
    val pokemonName: String,
    val imageUrl: String,
    val number: Int
)
