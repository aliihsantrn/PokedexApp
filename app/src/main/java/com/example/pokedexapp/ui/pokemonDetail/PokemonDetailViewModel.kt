package com.example.pokedexapp.ui.pokemonDetail

import androidx.lifecycle.ViewModel
import com.example.pokedexapp.data.remote.responses.Pokemon
import com.example.pokedexapp.repo.PokemonRepository
import com.example.pokedexapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
): ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String) : Resource<Pokemon> {
        return repository.getPokemonDetail(pokemonName)
    }


}