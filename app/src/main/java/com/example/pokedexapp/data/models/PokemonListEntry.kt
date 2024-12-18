package com.example.pokedexapp.data.models

import retrofit2.http.Url

data class PokemonListEntry(
    val pokemonName: String,
    val imageUrl: Url,
    val number: Int
)
