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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gyleedev.githubsearch.core.designsystem.theme.GithubSearchTheme
import kotlinx.coroutines.launch

@Composable
fun PulsingHeart(
    modifier: Modifier = Modifier,
    tint: Color = Color.Red,
    onClick: () -> Unit = {},
) {
    // 애니메이션 제어를 위한 CoroutineScope
    val scope = rememberCoroutineScope()

    // 심장 크기 조절을 위한 Animatable 상태 (기본값 1.0)
    val scale = remember { Animatable(1f) }

    // 클릭 시 리플 효과를 제거하기 위한 InteractionSource
    val interactionSource = remember { MutableInteractionSource() }

    Icon(
        imageVector = Icons.Default.Favorite,
        contentDescription = null,
        tint = tint,
        modifier =
            modifier
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                ) {
                    // 클릭 이벤트 콜백 실행
                    onClick()

                    // 애니메이션 실행 (박동 효과: 1.0 -> 1.3 -> 1.0)
                    scope.launch {
                        // 수축 및 팽창
                        scale.animateTo(
                            targetValue = 1.3f,
                            animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
                        )
                        // 원래 크기로 복원
                        scale.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
                        )
                    }
                },
    )
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
private fun PulsingHeartPreview() {
    GithubSearchTheme {
        Surface {
            Box(
                modifier =
                    Modifier
                        .size(100.dp)
                        .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                PulsingHeart(modifier = Modifier.size(48.dp))
            }
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PulsingHeartDarkPreview() {
    GithubSearchTheme {
        Surface {
            Box(
                modifier =
                    Modifier
                        .size(100.dp)
                        .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                PulsingHeart(modifier = Modifier.size(48.dp))
            }
        }
    }
}
