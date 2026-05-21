package com.gyleedev.githubsearch.core.designsystem.theme.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FlippableUserAvatar(
    avatar: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "avatarFlipAnimation",
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        contentAlignment = Alignment.Center,
    ) {
        if (rotation <= 90f) {
            UserAvatar(
                avatar = avatar,
                size = size,
            )
        } else {
            // Replicate UserAvatar's layout padding and size
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(size)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .graphicsLayer { rotationY = 180f },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(size / 2),
                )
            }
        }
    }
}
