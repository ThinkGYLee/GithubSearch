package com.gyleedev.githubsearch.core.designsystem.theme.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@Composable
fun UserAvatar(
    avatar: String,
    modifier: Modifier = Modifier,
) {
    GlideImage(
        imageModel = { avatar },
        modifier =
        modifier
            .padding(horizontal = 8.dp)
            .sizeIn(minWidth = 20.dp, minHeight = 20.dp, maxWidth = 80.dp, maxHeight = 80.dp)
            .clip(CircleShape),
        component =
        rememberImageComponent {
            +ShimmerPlugin(
                Shimmer.Flash(
                    baseColor = Color.White,
                    highlightColor = Color.LightGray,
                ),
            )
        },
    )
}
