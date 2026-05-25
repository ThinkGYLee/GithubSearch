package com.gyleedev.githubsearch.ui

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.nativePaint
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.cloudy.cloudy

@Composable
fun LiquidNavigationBar(
    currentRoute: String?,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navItems = remember {
        listOf(
            BottomNavItem.Home,
            BottomNavItem.Favorite,
            BottomNavItem.Setting,
        )
    }

    // 테마 시스템(ColorScheme) 기반 색상
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val surfaceContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val selectedIndex = navItems.indexOfFirst { it.screenRoute == currentRoute }.coerceAtLeast(0)

    val navBarPadding = 24.dp
    val navBarHeight = 64.dp
    val navBarVerticalPadding = 16.dp

    BoxWithConstraints(
        modifier = modifier
            .padding(horizontal = navBarPadding)
            .padding(bottom = navBarVerticalPadding)
            .fillMaxWidth()
            .height(navBarHeight),
        contentAlignment = Alignment.CenterStart,
    ) {
        val maxWidth = maxWidth
        val itemWidth = maxWidth / navItems.size

        val animatedOffsetX by animateDpAsState(
            targetValue = itemWidth * selectedIndex,
            animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
            label = "pill_movement_animation",
        )

        // 1. 배경 레이어 (Glassmorphism)
        val blurRadius = 200

        Surface(
            shape = CircleShape,
            color = surfaceContainerColor.copy(alpha = 0.85f),
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .cloudy(blurRadius),
        ) {}

        // 2. 선택 영역 캡슐 레이어: 수제 광원 효과 (liquidGlass 아티팩트 완전 제거)
        Box(
            modifier = Modifier
                .offset {
                    // State 기반 값은 람다 오버로드를 사용하여 불필요한 Recomposition 방지
                    IntOffset(x = animatedOffsetX.roundToPx(), y = 0)
                }
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .width(itemWidth - 8.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(28.dp))
                // 1) 캡슐 내부 입체감을 위한 베이스 그라데이션
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            onSurfaceColor.copy(alpha = 0.12f),
                            onSurfaceColor.copy(alpha = 0.02f),
                            onSurfaceColor.copy(alpha = 0.08f),
                        ),
                    ),
                )
                // 2) 사실적인 액체 유리 광택을 위한 Radial 광원 (좌상단 중심)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            // 광원 중심
                            Color.White.copy(alpha = 0.35f),
                            // 주변으로 확산
                            Color.Transparent,
                        ),
                        center = Offset(x = 100f, y = 50f),
                        radius = 450f,
                    ),
                )
                // 3) 상단 Rim Light (유리의 날카로운 반사면)
                .liquidInnerShadowStable(
                    blur = 2.dp,
                    color = Color.White.copy(alpha = 0.45f),
                    offsetY = (-1.2).dp,
                    borderRadius = 28.dp,
                )
                // 4) 전체적인 글로우 및 그림자
                .liquidInnerShadowStable(
                    blur = 10.dp,
                    color = onSurfaceColor.copy(alpha = 0.15f),
                    offsetY = 2.dp,
                    borderRadius = 28.dp,
                ),
        )

        // 3. 콘텐츠 레이어
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navItems.forEachIndexed { _, item ->
                val isSelected = item.screenRoute == currentRoute

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .selectable(
                            selected = isSelected,
                            onClick = { onClick(item.screenRoute) },
                            role = Role.Tab,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = item.icons,
                        contentDescription = stringResource(id = item.title),
                        tint = if (isSelected) onSurfaceColor else onSurfaceColor.copy(alpha = 0.4f),
                    )

                    Text(
                        text = stringResource(id = item.title),
                        color = if (isSelected) onSurfaceColor else onSurfaceColor.copy(alpha = 0.4f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}

/**
 * 안정적인 내부 그림자 구현체.
 * 렌더링 간섭을 피하기 위해 최적화된 Path 연산을 사용합니다.
 */
fun Modifier.liquidInnerShadowStable(
    blur: Dp = 4.dp,
    color: Color = Color.Black.copy(alpha = 0.2f),
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    borderRadius: Dp = 0.dp,
) = this.drawBehind {
    val size = size
    val borderRadiusPx = borderRadius.toPx()
    val blurPx = blur.toPx()

    drawIntoCanvas { canvas ->
        val paint = Paint()
        val nativePaint = paint.nativePaint

        paint.color = color
        paint.isAntiAlias = true
        if (blurPx > 0) {
            nativePaint.maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
        }

        canvas.save()
        val path = androidx.compose.ui.graphics.Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    radiusX = borderRadiusPx,
                    radiusY = borderRadiusPx,
                ),
            )
        }
        canvas.clipPath(path)

        val shadowPath = android.graphics.Path().apply {
            // 외부 여백을 충분히 두어 하드 엣지 방지
            val margin = blurPx * 3
            addRect(-margin, -margin, size.width + margin, size.height + margin, android.graphics.Path.Direction.CW)
            addRoundRect(
                offsetX.toPx(),
                offsetY.toPx(),
                size.width + offsetX.toPx(),
                size.height + offsetY.toPx(),
                borderRadiusPx,
                borderRadiusPx,
                android.graphics.Path.Direction.CCW,
            )
        }

        canvas.nativeCanvas.drawPath(shadowPath, nativePaint)
        canvas.restore()
    }
}
