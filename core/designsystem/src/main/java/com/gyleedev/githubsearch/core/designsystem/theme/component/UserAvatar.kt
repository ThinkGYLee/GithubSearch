package com.gyleedev.githubsearch.core.designsystem.theme.component

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.skydoves.landscapist.glide.GlideImageState
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
    var isSuccess by remember { mutableStateOf(false) }

    // 로딩 중에는 Shimmer 효과를 극대화하기 위해 투명(Transparent) 배경을 유지,
    // 완료 후에만 투명 아바타를 위해 흰색 배경으로 전환
    val backgroundColor by animateColorAsState(
        targetValue = if (isSuccess) Color.White else Color.Transparent,
        animationSpec = tween(durationMillis = 400),
        label = "avatarPlaceholderTransition",
    )

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
                drawCircle(backgroundColor, radius = size.toPx() / 2f - 0.5f)
            },
        contentAlignment = Alignment.Center,
    ) {
        GlideImage(
            imageModel = { avatar },
            onImageStateChanged = { state ->
                if (state is GlideImageState.Success) isSuccess = true
            },
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
