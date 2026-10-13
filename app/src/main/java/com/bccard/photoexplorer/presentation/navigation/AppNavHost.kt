package com.bccard.photoexplorer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bccard.photoexplorer.presentation.main.MainRoute

private const val MAIN_ROUTE = "main"

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MAIN_ROUTE,
        modifier = modifier,
    ) {
        composable(MAIN_ROUTE) {
            MainRoute()
        }
    }
}
