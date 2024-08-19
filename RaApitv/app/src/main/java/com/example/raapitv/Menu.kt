package com.example.raapitv

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.tv.material3.Text
import android.app.Activity
import androidx.compose.ui.platform.LocalContext

@Composable
fun Menu(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context as? Activity

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Menú de Opciones",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 24.dp)
        )
        MenuButton(text = "Lista de Películas", backgroundColor = Color(0xFF388E3C), onClick = { navController.navigate(Routes.MovieList.route) })
        MenuButton(text = "Acerca de", backgroundColor = Color(0xFF1E88E5), onClick = {  navController.navigate(Routes.AcercaDe.route) })
        MenuButton(text = "Salir", backgroundColor = Color(0xFFD32F2F), onClick = { activity?.finish() })
    }
}

@Composable
fun MenuButton(text: String, backgroundColor: Color, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 32.dp)
            .size(width = 280.dp, height = 50.dp)
            .padding(vertical = 16.dp, horizontal = 32.dp)
    )
}