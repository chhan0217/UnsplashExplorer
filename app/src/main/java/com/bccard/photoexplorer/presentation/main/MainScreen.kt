package com.bccard.photoexplorer.presentation.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
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
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.bccard.photoexplorer.R
import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.presentation.common.ErrorContent
import com.bccard.photoexplorer.presentation.common.LoadingContent
import com.bccard.photoexplorer.ui.theme.UnsplashExplorerTheme
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun MainRoute(
    viewModel: MainViewModel = hiltViewModel(),
    onFavoriteClick: () -> Unit,
    onPhotoClick: (String) -> Unit,
) {
    val savingPhotoIds by viewModel.savingPhotoIds.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    MainScreen(
        photos = viewModel.photos.collectAsLazyPagingItems(),
        savingPhotoIds = savingPhotoIds,
        errorMessage = errorMessage,
        onFavoriteClick = onFavoriteClick,
        onPhotoClick = onPhotoClick,
        onPhotoFavoriteClick = { photo ->
            viewModel.onPhotoFavorite(photo)
        },
        onErrorShown = viewModel::onErrorShown,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainScreen(
    photos: LazyPagingItems<Photo>,
    savingPhotoIds: Set<String>,
    errorMessage: String?,
    onFavoriteClick: () -> Unit,
    onPhotoFavoriteClick: (Photo) -> Unit,
    onPhotoClick: (String) -> Unit,
    onErrorShown: () -> Unit,
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
                title = { Text(stringResource(R.string.main_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                actions = {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_favorite_border),
                            contentDescription = null,
                            tint = Color.Unspecified,
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
        ) {
            when (val refresh = photos.loadState.refresh) {
                LoadState.Loading -> LoadingContent()
                is LoadState.Error -> ErrorContent(
                    message = refresh.error.message ?: "사진을 불러오지 못했습니다.",
                    onRetry = photos::retry,
                )

                is LoadState.NotLoading -> PhotoList(
                    photos = photos,
                    savingPhotoIds = savingPhotoIds,
                    onPhotoFavoriteClick = onPhotoFavoriteClick,
                    onPhotoClick = onPhotoClick,
                )
            }
        }
    }
}

@Composable
private fun PhotoList(
    photos: LazyPagingItems<Photo>,
    savingPhotoIds: Set<String>,
    onPhotoFavoriteClick: (Photo) -> Unit,
    onPhotoClick: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            count = photos.itemCount,
            key = photos.itemKey { photo -> photo.id },
        ) { index ->
            val photo = photos[index] ?: return@items
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                val placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AsyncImage(
                        model = photo.imageUrl,
                        contentDescription = null,
                        placeholder = placeholder,
                        error = placeholder,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .clickable {
                                onPhotoClick(photo.id)
                            },
                        contentScale = ContentScale.Crop,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 4.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = photo.photographerProfileImageUrl,
                            contentDescription = null,
                            placeholder = placeholder,
                            error = placeholder,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = photo.photographerName,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        IconButton(
                            onClick = { onPhotoFavoriteClick(photo) },
                            modifier = Modifier.size(32.dp),
                            enabled = photo.id !in savingPhotoIds,
                        ) {
                            if (photo.id in savingPhotoIds) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            } else {
                                Icon(
                                    painter = painterResource(
                                        if (photo.isFavorite) {
                                            R.drawable.ic_favorite_filled
                                        } else {
                                            R.drawable.ic_favorite_border
                                        },
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Unspecified,
                                )
                            }
                        }
                    }
                }
            }
        }

        when (val append = photos.loadState.append) {
            LoadState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) {
                LoadingContent()
            }

            is LoadState.Error -> item(span = { GridItemSpan(maxLineSpan) }) {
                ErrorContent(
                    message = append.error.message ?: "사진을 더 불러오지 못했습니다.",
                    onRetry = photos::retry,
                )
            }

            is LoadState.NotLoading -> Unit
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainPreview() {
    val photos = flowOf(
        PagingData.from(
            listOf(
                Photo(
                    id = "preview-1",
                    description = null,
                    imageUrl = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee",
                    photographerName = "Queensland, Australia",
                    photographerProfileImageUrl = "https://images.unsplash.com/profile-1789494943253-10d21358fb57image?crop=faces&fit=crop&w=64&h=64",
                ),
                Photo(
                    id = "preview-2",
                    description = null,
                    imageUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e",
                    photographerName = "Valentino Mazzariello",
                    photographerProfileImageUrl = "https://images.unsplash.com/profile-1791834464107-8912b6df2a2c?crop=faces&fit=crop&w=64&h=64",
                    isFavorite = true,
                ),
                Photo(
                    id = "preview-3",
                    description = null,
                    imageUrl = "https://images.unsplash.com/photo-1470770841072-f978cf4d019e",
                    photographerName = "Haotian Zheng",
                    photographerProfileImageUrl = "https://images.unsplash.com/profile-1722333889497-c5b0ffe5b1b5?crop=faces&fit=crop&w=64&h=64",
                ),
                Photo(
                    id = "preview-4",
                    description = null,
                    imageUrl = "https://images.unsplash.com/photo-1501785888041-af3ef285b470",
                    photographerName = "Nature Photographer",
                    photographerProfileImageUrl = null,
                ),
            ),
        ),
    ).collectAsLazyPagingItems()

    UnsplashExplorerTheme {
        MainScreen(
            photos = photos,
            savingPhotoIds = emptySet(),
            errorMessage = null,
            onFavoriteClick = {},
            onPhotoClick = {},
            onPhotoFavoriteClick = {},
            onErrorShown = {},
        )
    }
}
