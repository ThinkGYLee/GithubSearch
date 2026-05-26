package com.gyleedev.githubsearch.core.designsystem.theme.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gyleedev.githubsearch.core.designsystem.theme.GithubSearchTheme
import kotlinx.coroutines.launch

@Composable
fun FavoritePulsingHeart(
    isFavorited: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeTint: Color = Color.Red,
    inactiveTint: Color = MaterialTheme.colorScheme.onSurface,
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isFavorited) {
        launch {
            scale.animateTo(
                targetValue = 1.3f,
                animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
            )
        }
    }

    val icon = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder
    val tint = if (isFavorited) activeTint else inactiveTint
    val interactionSource = remember { MutableInteractionSource() }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
    )
}

@Preview(showBackground = true, name = "Favorited")
@Composable
private fun FavoritePulsingHeartFavoritedPreview() {
    GithubSearchTheme {
        Surface {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                FavoritePulsingHeart(isFavorited = true, onClick = {}, modifier = Modifier.size(48.dp))
            }
        }
    }
}

@Preview(showBackground = true, name = "Unfavorited")
@Composable
private fun FavoritePulsingHeartUnfavoritedPreview() {
    GithubSearchTheme {
        Surface {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                FavoritePulsingHeart(isFavorited = false, onClick = {}, modifier = Modifier.size(48.dp))
            }
        }
    }
}
