package com.gyleedev.githubsearch.core.designsystem.theme.component

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateFloatAsState
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

    // 1. 화면 전환 상태 감지
    val transition = animatedVisibilityScope?.transition
    val isTransitionFinished = transition?.let {
        it.currentState == it.targetState
    } ?: true

    // 2. 현재 화면이 나타나는 중(Entering)인지 확인
    val isEntering = transition?.targetState == EnterExitState.Visible

    // 3. 투명도 로직 최적화:
    // - 출발지(Exiting)일 때는 배경을 유지(1f)하여 깜빡임 방지
    // - 도착지(Entering)일 때는 전환 완료 후 배경 노출(0f -> 1f)
    val targetAlpha = if (isSuccess) {
        if (isEntering) {
            if (isTransitionFinished) 1f else 0f
        } else {
            1f // 사라지는 화면에서는 배경을 그대로 둠
        }
    } else {
        0f
    }

    val backgroundAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 400),
        label = "avatarBackgroundAlphaTransition",
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
                // 투명도(alpha)를 적용하여 전환 완료 후에만 배경색이 보이도록 정밀 제어
                drawCircle(
                    color = Color.White.copy(alpha = backgroundAlpha),
                    radius = size.toPx() / 2f - 0.5f,
                )
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
