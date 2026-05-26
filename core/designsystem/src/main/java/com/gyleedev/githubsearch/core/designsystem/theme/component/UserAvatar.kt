package com.gyleedev.githubsearch.core.designsystem.theme.component

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gyleedev.githubsearch.core.designsystem.LocalAnimatedVisibilityScope
import com.gyleedev.githubsearch.core.designsystem.LocalSharedTransitionScope
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun UserAvatar(
    avatar: String,
    login: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    val sharedElementModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                rememberSharedContentState(key = "avatar-$login"),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = { _, _ ->
                    tween(durationMillis = 500)
                },
                clipInOverlayDuringTransition = OverlayClip(CircleShape),
            )
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size)
            .drawBehind {
                // 배경 원을 0.5px 미세하게 작게 그려 이미지 외곽 번짐(Bleeding) 방지
                drawCircle(Color.White, radius = size.toPx() / 2f - 0.5f)
            },
        contentAlignment = Alignment.Center,
    ) {
        GlideImage(
            imageModel = { avatar },
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .then(sharedElementModifier),
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
}
