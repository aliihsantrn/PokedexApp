package com.example.pokedexapp.ui.pokemonList

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.capitalize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import coil3.Image
import com.example.pokedexapp.data.models.PokemonListModel
import com.example.pokedexapp.repo.PokemonRepository
import com.example.pokedexapp.util.Constants
import com.example.pokedexapp.util.Constants.LOAD_LIMIT
import com.example.pokedexapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    var currentPage = 0
    var pokemonList = mutableStateOf<List<PokemonListModel>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    init {
        loadPokemonList()
    }

    fun loadPokemonList() {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getPokemonList(LOAD_LIMIT, currentPage * LOAD_LIMIT)
            when(result) {
                is Resource.Success -> {
                    endReached.value = currentPage * LOAD_LIMIT >=
                    result.data!!.count

                    val pokemonData = result.data.results.mapIndexed { index, data ->
                        // "url": "https://pokeapi.co/api/v2/pokemon/1/" -> num = 1
                        val number = if (data.url.endsWith("/")) {
                            data.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            data.url.takeLastWhile { it.isDigit() }
                        }

                        val image_url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokemonListModel(
                            data.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                            image_url,
                            number.toInt()
                        )
                    }

                    currentPage++
                    pokemonList.value += pokemonData
                    isLoading.value = false
                    loadError.value = ""
                }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }
                else -> {
                    loadError.value = "An unknown error occurred."
                    isLoading.value = false
                }
            }

        }
    }

    fun calcDominantColor(drawable: Drawable, onColorCalculated: (Color) -> Unit) {
        val bitmap = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bitmap).generate { palette ->
            val dominantColor = palette?.dominantSwatch?.rgb ?: Color.Gray.toArgb()
            onColorCalculated(Color(dominantColor))
        }
    }

}