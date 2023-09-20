package ru.vtb24.mobilebanking.android.locator.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import ru.vtb24.mobilebanking.android.locator.presentation.theme.AppTheme

@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderColor: Color = AppTheme.colors.secondary,
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        placeholder = ColorPainter(placeholderColor),
        modifier = modifier,
        contentScale = contentScale
    )
}
