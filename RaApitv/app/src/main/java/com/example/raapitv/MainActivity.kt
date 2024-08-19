package com.example.raapitv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.raapitv.MovieDetailScreen
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navigationController = rememberNavController()
            NavHost(navController = navigationController, startDestination = Routes.Menu.route) {
                composable(Routes.Menu.route) { Menu(navController = navigationController) }
                composable(Routes.AcercaDe.route) { AcercaDe(navController = navigationController) }
                composable(Routes.MovieList.route) { MovieList(navController = navigationController) }

                composable("movie_detail/{movieId}") { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
                    MovieDetailScreen(movieId = movieId, navController = navigationController)
                }
            }
        }
    }
}


// Retrofit API Interface
interface MovieApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("api_key") apiKey: String): MovieResponse

    @GET("movie/{movieId}")
    suspend fun getMovieDetail(@Query("api_key") apiKey: String, @Path("movieId") movieId: String): MovieDetail
}

// Retrofit Repository
class MovieRepository {
    private val apiKey = "ef4e3bedf857ae49d0ccb1d1c66fc113" // Reemplaza con tu API Key

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val movieApi = retrofit.create(MovieApi::class.java)

    suspend fun getPopularMovies() = movieApi.getPopularMovies(apiKey)
    suspend fun getMovieDetail(movieId: String) = movieApi.getMovieDetail(apiKey, movieId)
}

// Data Models
data class MovieResponse(
    @SerializedName("results") val results: List<Movie>
)

data class Movie(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("vote_average") val voteAverage: String,
    @SerializedName("popularity") val popularity: String,
    @SerializedName("vote_count") val voteCount: String,
    @SerializedName("original_language") val originalLanguage: String,
)

data class MovieDetail(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("vote_average") val voteAverage: String
)


// Movie List Screen
@Composable
fun MovieList(navController: NavController) {
    val movieRepository = remember { MovieRepository() }
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val response = movieRepository.getPopularMovies()
            movies = response.results
        } catch (e: Exception) {
            // Manejo de errores
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Películas Populares",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,


        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            items(movies) { movie ->
                MovieItem(movie = movie) {
                    navController.navigate("movie_detail/${movie.id}")
                }
            }
        }
    }
}

// Movie Item
@Composable
fun MovieItem(movie: Movie, onClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
        .clickable (onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberImagePainter("https://image.tmdb.org/t/p/w500${movie.posterPath}"),
                contentDescription = movie.title,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                androidx.tv.material3.Text(text = "Titulo: ${movie.title}", style = androidx.tv.material3.MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                androidx.tv.material3.Text(text = "Id: ${movie.id}", style = androidx.tv.material3.MaterialTheme.typography.titleMedium)
                androidx.tv.material3.Text(text = "Fecha de estreno: ${movie.releaseDate}", style = androidx.tv.material3.MaterialTheme.typography.titleMedium)
                androidx.tv.material3.Text(text = "Puntuación: ${movie.voteAverage}", style = androidx.tv.material3.MaterialTheme.typography.titleMedium)
                androidx.tv.material3.Text(text = "Popularidad: ${movie.popularity}", style = androidx.tv.material3.MaterialTheme.typography.titleMedium)
                androidx.tv.material3.Text(text = "Número de votos recibidos: ${movie.voteCount}", style = androidx.tv.material3.MaterialTheme.typography.titleMedium)
                androidx.tv.material3.Text(text = "Idioma original: ${movie.originalLanguage}", style = androidx.tv.material3.MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                androidx.tv.material3.Text(text = "Descripción: ${movie.overview}", style = androidx.tv.material3.MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun MovieDetailScreen(movieId: String, navController: NavController) {
    val movieRepository = remember { MovieRepository() }
    var movieDetail by remember { mutableStateOf<MovieDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(movieId) {
        try {
            val response = movieRepository.getMovieDetail(movieId)
            movieDetail = response
        } catch (e: Exception) {
            isLoading = false
            println("Error al obtener detalles de la película: ${e.message}")
        }
    }

    if (isLoading) {
        // Mostrar un indicador de carga mientras los datos se obtienen
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Cargando...", style = MaterialTheme.typography.bodyLarge)
        }
    } else if (movieDetail != null) {
        // Mostrar el detalle de la película si los datos se obtienen con éxito
        val movie = movieDetail!!
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberImagePainter("https://image.tmdb.org/t/p/w500${movie.posterPath}"),
                contentDescription = movie.title,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Fecha de estreno: ${movie.releaseDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Puntuación: ${movie.voteAverage}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movie.overview,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        // Mostrar un mensaje de error si no se pudo obtener el detalle de la película
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Error al cargar los detalles de la película.", color = Color.Red)
        }
    }
}

