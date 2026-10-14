package com.bccard.photoexplorer.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bccard.photoexplorer.presentation.detail.DetailRoute
import com.bccard.photoexplorer.presentation.main.MainRoute

private const val MAIN_ROUTE = "main"
private const val PHOTO_ID_ARGUMENT = "photoId"
private const val DETAIL_ROUTE = "detail/{$PHOTO_ID_ARGUMENT}"

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MAIN_ROUTE,
        modifier = modifier,
    ) {
        composable(MAIN_ROUTE) {
            MainRoute(
                onPhotoClick = { photoId ->
                    navController.navigate("detail/${Uri.encode(photoId)}")
                },
            )
        }
        composable(
            route = DETAIL_ROUTE,
            arguments = listOf(
                navArgument(PHOTO_ID_ARGUMENT) { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments
                ?.getString(PHOTO_ID_ARGUMENT)
                ?: return@composable

            DetailRoute(
                photoId = photoId,
                onBackClick = navController::popBackStack,
                onFavoriteClick = {},
            )
        }
    }
}
