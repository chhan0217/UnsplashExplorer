package com.bccard.photoexplorer.presentation.favorite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.bccard.photoexplorer.R
import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.presentation.common.ErrorContent
import com.bccard.photoexplorer.presentation.common.LoadingContent
import com.bccard.photoexplorer.ui.theme.UnsplashExplorerTheme
import java.io.File

@Composable
internal fun FavoriteRoute(
    viewModel: FavoriteViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onPhotoClick: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    FavoriteScreen(
        uiState = uiState,
        errorMessage = errorMessage,
        onBackClick = onBackClick,
        onPhotoClick = onPhotoClick,
        onPhotoFavoriteClick = viewModel::removeFavoritePhoto,
        onErrorShown = viewModel::onErrorShown,
        onRetry = viewModel::retryFavorites,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteScreen(
    uiState: FavoriteUiState,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onPhotoClick: (String) -> Unit,
    onPhotoFavoriteClick: (Photo) -> Unit,
    onErrorShown: () -> Unit,
    onRetry: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onErrorShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.favorite_title)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        when (uiState) {
            FavoriteUiState.Loading -> LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )

            is FavoriteUiState.Success -> {
                if (uiState.photos.isEmpty()) {
                    EmptyContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    )
                } else {
                    PhotoList(
                        photos = uiState.photos,
                        onPhotoClick = onPhotoClick,
                        onPhotoFavoriteClick = onPhotoFavoriteClick,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    )
                }
            }

            is FavoriteUiState.Error -> ErrorContent(
                message = uiState.message,
                onRetry = onRetry,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
        }
    }
}

@Composable
private fun PhotoList(
    photos: List<Photo>,
    onPhotoClick: (String) -> Unit,
    onPhotoFavoriteClick: (Photo) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = photos,
            key = Photo::id,
        ) { photo ->
            FavoritePhotoCard(
                photo = photo,
                onClick = { onPhotoClick(photo.id) },
                onPhotoFavoriteClick = { onPhotoFavoriteClick(photo) }
            )
        }
    }
}

@Composable
private fun FavoritePhotoCard(
    photo: Photo,
    onClick: () -> Unit,
    onPhotoFavoriteClick: (Photo) -> Unit,
) {
    val placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant)
    val localImage = photo.localImagePath
        ?.let(::File)
        ?.takeIf(File::exists)

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column {
            AsyncImage(
                model = localImage ?: photo.imageUrl,
                contentDescription = null,
                placeholder = placeholder,
                error = placeholder,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clickable(onClick = onClick),
                contentScale = ContentScale.Crop,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = photo.photographerName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    painter = painterResource(R.drawable.ic_favorite_filled),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            onPhotoFavoriteClick(photo)
                        },
                    tint = Color.Unspecified,
                )
            }
        }
    }
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.favorite_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritePreview() {
    UnsplashExplorerTheme {
        FavoriteScreen(
            uiState = FavoriteUiState.Success(
                photos = listOf(
                    Photo(
                        id = "preview",
                        description = "Favorite preview",
                        imageUrl = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee",
                        photographerName = "Photographer",
                        isFavorite = true,
                    ),
                ),
            ),
            errorMessage = null,
            onBackClick = {},
            onPhotoClick = {},
            onPhotoFavoriteClick = {},
            onErrorShown = {},
            onRetry = {},
        )
    }
}
