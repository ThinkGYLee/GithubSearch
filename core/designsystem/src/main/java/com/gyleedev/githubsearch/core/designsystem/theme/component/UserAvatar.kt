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
    transitionKeyPrefix: String = "",
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    var isSuccess by remember(avatar) { mutableStateOf(false) }

    // 1. 화면 전환 상태 감지
    val transition = animatedVisibilityScope?.transition

    // 2. 현재 화면이 "완전히" 정지해 있는 상태인지 확인 (전환 중이 아님)
    val isIdle =
        transition?.let {
            it.currentState == it.targetState && it.currentState == EnterExitState.Visible
        } ?: false

    // 3. 투명도 로직: 전환 중이거나 로딩 중이면 배경을 숨김
    val targetAlpha = if (isSuccess && isIdle) 1f else 0f
    val backgroundAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        // 사라질 때(300ms)와 나타날 때(400ms) 모두 부드럽게 처리
        animationSpec = if (targetAlpha == 0f) tween(durationMillis = 300) else tween(durationMillis = 400),
        label = "avatarBackgroundAlphaTransition",
    )

    val sharedBoundsModifier =
        if (sharedTransitionScope != null && animatedVisibilityScope != null) {
            with(sharedTransitionScope) {
                val key = if (transitionKeyPrefix.isEmpty()) "avatar-$login" else "$transitionKeyPrefix-avatar-$login"
                Modifier.sharedBounds(
                    rememberSharedContentState(key = key),
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
        modifier =
            modifier
                .size(size)
                .then(sharedBoundsModifier)
                .drawBehind {
                    // 투명도가 어느 정도 있을 때만 배경 원을 그림
                    if (backgroundAlpha > 0.01f) {
                        drawCircle(
                            color = Color.White.copy(alpha = backgroundAlpha),
                            radius = size.toPx() / 2f - 0.5f,
                        )
                    }
                },
        contentAlignment = Alignment.Center,
    ) {
        GlideImage(
            imageModel = { avatar },
            onImageStateChanged = { state ->
                if (state is GlideImageState.Success) isSuccess = true
            },
            modifier =
                Modifier
                    .fillMaxSize()
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
}
