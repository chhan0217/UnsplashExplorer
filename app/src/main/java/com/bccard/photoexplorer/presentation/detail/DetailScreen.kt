package com.bccard.photoexplorer.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.bccard.photoexplorer.R
import com.bccard.photoexplorer.domain.model.PhotoDetail
import com.bccard.photoexplorer.domain.model.PhotoExif
import com.bccard.photoexplorer.domain.model.PhotoLocation
import com.bccard.photoexplorer.domain.model.Photographer
import com.bccard.photoexplorer.presentation.common.ErrorContent
import com.bccard.photoexplorer.presentation.common.LoadingContent
import com.bccard.photoexplorer.ui.theme.UnsplashExplorerTheme

@Composable
internal fun DetailRoute(
    viewModel: DetailViewModel = hiltViewModel(),
    photoId: String,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(photoId) {
        viewModel.loadPhotoDetail(photoId)
    }

    DetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onFavoriteClick = onFavoriteClick,
        onRetry = { viewModel.loadPhotoDetail(photoId) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(
    uiState: DetailUiState,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detail_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_favorite),
                            contentDescription = stringResource(R.string.favorites),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is DetailUiState.Loading -> {
                LoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }

            is DetailUiState.Success -> {
                DetailContent(
                    photoDetail = uiState.photoDetail,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is DetailUiState.Error -> {
                ErrorContent(
                    message = uiState.message,
                    onRetry = onRetry,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    photoDetail: PhotoDetail,
    modifier: Modifier = Modifier,
) {
    val placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant)
    val description = photoDetail.description
        ?.takeIf(String::isNotBlank)
        ?: stringResource(R.string.detail_no_description)
    val imageAspectRatio = if (photoDetail.width > 0 && photoDetail.height > 0) {
        (photoDetail.width.toFloat() / photoDetail.height).coerceIn(0.75f, 1.5f)
    } else {
        4f / 3f
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        AsyncImage(
            model = photoDetail.imageUrl,
            contentDescription = photoDetail.description,
            placeholder = placeholder,
            error = placeholder,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(imageAspectRatio)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = photoDetail.photographer.profileImageUrl,
                contentDescription = photoDetail.photographer.name,
                placeholder = placeholder,
                error = placeholder,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = photoDetail.photographer.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                photoDetail.photographer.instagramUsername
                    ?.takeIf(String::isNotBlank)
                    ?.let { username ->
                        Text(
                            text = stringResource(R.string.detail_instagram_username, username),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
            }
        }

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(20.dp))
        if (photoDetail.tags.isNotEmpty()) {
            Text(
                text = stringResource(R.string.detail_tags),
                modifier = Modifier.padding(bottom = 10.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(photoDetail.tags) { tag ->
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DetailPreview() {
    UnsplashExplorerTheme {
        DetailScreen(
            uiState = DetailUiState.Success(photoDetail = previewPhotoDetail),
            onBackClick = {},
            onFavoriteClick = {},
            onRetry = {},
        )
    }
}

private val previewPhotoDetail = PhotoDetail(
    id = "LBI7cgq3pbM",
    description = "해 질 무렵 산과 호수가 어우러진 고요한 풍경",
    imageUrl = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee",
    width = 6000,
    height = 4000,
    color = "#6E7B83",
    likes = 3_421,
    views = 1_284_320,
    downloads = 48_291,
    photographer = Photographer(
        username = "alexmorgan",
        name = "Alex Morgan",
        instagramUsername = "alexmorgan.photo",
        profileImageUrl = "https://images.unsplash.com/profile-1500530855697-b586d89ba3ee",
    ),
    exif = PhotoExif(
        make = "SONY",
        model = "ILCE-7M4",
        name = "Sony Alpha 7 IV",
        exposureTime = "1/250",
        aperture = "2.8",
        focalLength = "35.0",
        iso = 100,
    ),
    location = PhotoLocation(
        name = "Lake Louise, Alberta, Canada",
        city = "Lake Louise",
        country = "Canada",
        latitude = 51.4254,
        longitude = -116.1773,
    ),
    tags = listOf(
        "nature",
        "mountain",
        "lake",
    ),
)
