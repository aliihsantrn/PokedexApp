package com.example.pokedexapp.ui.pokemonList

import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.asDrawable
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import com.example.pokedexapp.R
import com.example.pokedexapp.data.models.PokemonListModel
import com.example.pokedexapp.ui.theme.RobotoCondensed

@Composable
fun PokemonListScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) {
        ImageResizeOnScrollExample(
            modifier = modifier,
            navController = navController
        )
    }
}

@Composable
fun ImageResizeOnScrollExample(
    modifier: Modifier = Modifier,
    maxImageSize: Dp = 250.dp,
    minImageSize: Dp = 100.dp,
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    var currentImageSize by remember { mutableStateOf(maxImageSize) }
    var imageScale by remember { mutableFloatStateOf(1f) }
    var searchBarAlpha by remember { mutableFloatStateOf(1f) }
    var searchBarTranslationY by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newImageSize = currentImageSize + delta.dp
                val previousImageSize = currentImageSize

                // Constrain the image size
                currentImageSize = newImageSize.coerceIn(minImageSize, maxImageSize)
                val consumed = currentImageSize - previousImageSize

                // Update scale for the image
                imageScale = currentImageSize / maxImageSize

                // Adjust SearchBar opacity and translation
                val progress = (currentImageSize - minImageSize) / (maxImageSize - minImageSize)

                searchBarAlpha = progress
                searchBarTranslationY = (1f - progress) * 50f

                return Offset(0f, consumed.value)
            }
        }
    }

    Box(modifier = modifier
        .nestedScroll(nestedScrollConnection)
    ) {
        Image(
            painter = painterResource(R.drawable.pokemon_logo),
            contentDescription = "Pokemon Logo",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .size(maxImageSize)
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    scaleX = imageScale
                    scaleY = imageScale
                    // Center the image vertically as it scales
                    translationY = -(maxImageSize.toPx() - currentImageSize.toPx()) / 3f
                }
        )
        Box(modifier = modifier
            .offset {
                IntOffset(0, (currentImageSize.roundToPx()))
            }
        ) {
            Column {
                SearchBar(
                    hint = "Search...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .graphicsLayer {
                            alpha = searchBarAlpha
                            translationY = searchBarTranslationY
                        }
                        .then(
                            if (searchBarAlpha == 0f) {
                                Modifier.height(0.dp)
                            } else {
                                Modifier
                            }
                        )
                ) {
                    viewModel.searchPokemon(it)
                }
                Spacer(modifier = Modifier.height(8.dp))
                DataList(
                    navController = navController
                )
            }

        }
    }
}


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    Box(
        modifier = modifier
    ) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            modifier = modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused
                }
        )

        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun PokemonCard(
    modifier: Modifier = Modifier,
    model : PokemonListModel,
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
    ) {
    val defaultDominantColor = MaterialTheme.colorScheme.surface

    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box (
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .shadow(5.dp, RoundedCornerShape(8.dp))
            .aspectRatio(1f)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .clickable {
                println(dominantColor)
                navController.navigate(
                    "pokemon_detail_screen/${dominantColor.toArgb()}/${model.pokemonName}"
                )
            }
            .fillMaxSize()
    ){
        Column(
            modifier = modifier
                .align(Alignment.Center)
        ) {

            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(model.imageUrl)
                    .build(),
                contentDescription = "Loaded image",
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.CenterHorizontally)
                    .weight(0.8f),
                loading = {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.scale(0.5f)
                    )
                },
                onSuccess = {
                    val drawable = it.result.image.asDrawable(Resources.getSystem())
                    viewModel.calcDominantColor(drawable = drawable) { color ->
                        dominantColor = color
                    }

                },
                error = {
                    Text(
                        "Problem has occurred ",
                        color = Color.Gray
                    )
                }
            )
            Text(
                text = model.pokemonName,
                fontFamily = RobotoCondensed,
                color = Color.Black,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .weight(0.18f)
            )
        }

    }
}

@Composable
fun DataList(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached}
    val loadError by remember { viewModel.loadError}
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching }


    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        content = {

            items(pokemonList.size) { index ->
                val pokemon = pokemonList[index]
                println(pokemon.pokemonName)

                PokemonCard(
                    model = pokemon,
                    navController = navController,
                    viewModel = viewModel,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                )

                if (index >= pokemonList.size - 1 && !endReached && !isSearching) {
                    viewModel.loadPokemonList()
                }
            }
        }
    )
}