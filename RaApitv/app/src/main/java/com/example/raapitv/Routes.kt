package com.example.raapitv

sealed class Routes (val route: String){
    object AcercaDe: Routes("acercade")
    object Menu: Routes("menu")
    object MovieList: Routes("muvie_list")
    /*object MovieDetailScreen : Routes("movie_detail/{movieId}")*/
}