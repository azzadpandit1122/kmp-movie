package ui.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ui.component.text.SubtitlePrimary
import ui.component.text.SubtitleSecondary
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.circular.CircularRevealPlugin
import com.skydoves.landscapist.coil3.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import data.model.MovieItem
import data.model.artist.Artist
import data.model.artist.Cast
import data.model.moviedetail.MovieDetail
import moe.tlaster.precompose.navigation.Navigator
import theme.DefaultBackgroundColor
import theme.FontColor
import theme.cornerRadius
import ui.component.shimmerBackground
import utils.AppConstant
import utils.AppString
import utils.hourMinutes
import utils.network.DataState
import utils.roundTo

@Composable
fun MovieDetail(
    navigator: Navigator,
    movieId: Int,
    movieDetailViewModel: MovieDetailViewModel = viewModel { MovieDetailViewModel() }
) {
    val isLoading = remember { mutableStateOf(false) }
    val movieDetail = remember { mutableStateOf<MovieDetail?>(null) }
    val recommendMovie = remember { mutableStateOf<List<MovieItem>?>(null) }
    val movieCredit = remember { mutableStateOf<Artist?>(null) }


    LaunchedEffect(Unit) {
        movieDetailViewModel.movieDetail(movieId)
        movieDetailViewModel.recommendedMovie(movieId)
        movieDetailViewModel.movieCredit(movieId)
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(
            DefaultBackgroundColor
        ), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        movieDetail.value?.let {
            UiDetail(it)
        }
        Column(
            modifier = Modifier.padding(
                start = 8.dp, end = 8.dp, top = 5.dp, bottom = 5.dp
            )
        ) {
            recommendMovie.value?.let {
                RecommendedMovie(navigator, it)
            }
            movieCredit.value?.let {
                ArtistAndCrew(navigator, it.cast)
            }
        }
    }
    movieDetailViewModel.movieDetail.collectAsState().value.let {
        when (it) {
            is DataState.Loading -> {
                isLoading.value = true
            }

            is DataState.Success<MovieDetail> -> {
                movieDetail.value = it.data
                isLoading.value = false
            }

            is DataState.Error -> {
                isLoading.value = false
            }
        }
    }
    movieDetailViewModel.recommendedMovie.collectAsState().value.let {
        when (it) {
            is DataState.Loading -> {
                isLoading.value = true
            }

            is DataState.Success -> {
                recommendMovie.value = it.data
                isLoading.value = false
            }

            is DataState.Error -> {
                isLoading.value = false
            }
        }
    }
    movieDetailViewModel.movieCredit.collectAsState().value.let {
        when (it) {
            is DataState.Loading -> {
                isLoading.value = true
            }

            is DataState.Success -> {
                movieCredit.value = it.data
                isLoading.value = false
            }

            is DataState.Error -> {
                isLoading.value = false
            }
        }
    }
}

@Composable
fun UiDetail(data: MovieDetail) {
    Column {
        CoilImage(
            imageModel = {
                AppConstant.IMAGE_URL.plus(
                    data.poster_path
                )
            },
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                contentDescription = "Movie item",
                colorFilter = null,
            ),
            component = rememberImageComponent {
                +CircularRevealPlugin(
                    duration = 800
                )
            },
            modifier = Modifier.fillMaxWidth().height(300.dp).shimmerBackground(
                RoundedCornerShape(5.dp)
            ),
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(start = 10.dp, end = 10.dp)
        ) {
            Text(
                text = data.title,
                modifier = Modifier.padding(top = 10.dp),
                color = FontColor,
                fontSize = 30.sp,
                fontWeight = FontWeight.W700,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp, top = 10.dp)
            ) {

                Column(Modifier.weight(1f)) {
                    SubtitlePrimary(
                        text = data.original_language,
                    )
                    SubtitleSecondary(
                        text = AppString.LANGUAGE
                    )
                }
                Column(Modifier.weight(1f)) {
                    SubtitlePrimary(
                        text = data.vote_average.roundTo(1).toString(),
                    )
                    SubtitleSecondary(
                        text = AppString.RATING
                    )
                }
                Column(Modifier.weight(1f)) {
                    SubtitlePrimary(
                        text = data.runtime.hourMinutes()
                    )
                    SubtitleSecondary(
                        text = AppString.DURATION
                    )
                }
                Column(Modifier.weight(1f)) {
                    SubtitlePrimary(
                        text = data.release_date
                    )
                    SubtitleSecondary(
                        text = AppString.RELEASE_DATE
                    )
                }
            }
            Text(
                text = AppString.DESCRIPTION,
                color = FontColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = data.overview)
        }
    }
}


@Composable
fun RecommendedMovie(navigator: Navigator?, recommendedMovie: List<MovieItem>) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(
            text = "Similar Movie",
            color = FontColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
        LazyRow(modifier = Modifier.fillMaxHeight()) {
            items(recommendedMovie, itemContent = { item: MovieItem ->
                Column(
                    modifier = Modifier.padding(
                        start = 0.dp, end = 8.dp, top = 5.dp, bottom = 5.dp
                    )
                ) {
                    CoilImage(
                        modifier = Modifier.height(190.dp).width(140.dp).cornerRadius(10)
                            .clickable {},
                        imageModel = { AppConstant.IMAGE_URL.plus(item.poster_path) },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            contentDescription = "Similar movie",
                            colorFilter = null,
                        ),
                        component = rememberImageComponent {
                            +CircularRevealPlugin(
                                duration = 800
                            )
                        },
                    )
                }
            })

        }
    }
}

@Composable
fun ArtistAndCrew(navigator: Navigator?, cast: List<Cast>) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(
            text = "Cast", color = FontColor, fontSize = 17.sp, fontWeight = FontWeight.SemiBold
        )
        LazyRow(modifier = Modifier.fillMaxHeight()) {
            items(cast, itemContent = { item ->
                Column(
                    modifier = Modifier.padding(
                        start = 0.dp, end = 10.dp, top = 5.dp, bottom = 5.dp
                    ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CoilImage(
                        modifier = Modifier.padding(bottom = 5.dp).height(80.dp).width(80.dp)
                            .cornerRadius(40).clickable {},
                        imageModel = { AppConstant.IMAGE_URL.plus(item.profile_path) },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            contentDescription = "artist and crew",
                            colorFilter = null,
                        ),
                        component = rememberImageComponent {
                            +CircularRevealPlugin(
                                duration = 800
                            )
                        },
                    )
                    SubtitleSecondary(text = item.name)
                }
            })
        }
    }
}

