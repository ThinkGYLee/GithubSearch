package com.gyleedev.githubsearch.ui

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gyleedev.githubsearch.core.designsystem.component.LiquidNavBarDefaults
import com.skydoves.cloudy.cloudy
import com.skydoves.cloudy.liquidGlass

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

    // 라이트/다크 모드에 따른 지능형 광원 색상 선택
    val isLightMode = MaterialTheme.colorScheme.surface.luminance() > 0.5f
    val highlightColor = if (isLightMode) {
        // 라이트 모드: 순수 흰색보다는 테마의 가장 밝은 배경색(Surface)을 기반으로 한 선명한 하이라이트
        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    } else {
        // 다크 모드: 푸른 기를 뺀 중립적이고 차분한 화이트 톤 (OnSurfaceVariant 활용)
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
    }

    val navBarPadding = 24.dp
    val navBarHeight = LiquidNavBarDefaults.Height

    BoxWithConstraints(
        modifier = modifier
            .padding(horizontal = navBarPadding)
            .padding(bottom = LiquidNavBarDefaults.BottomMargin) // 부모 컨테이너가 시스템 바 위에 있으므로 단순 마진만 적용
            .fillMaxWidth()
            .height(navBarHeight),
        contentAlignment = Alignment.CenterStart,
    ) {
        val maxWidth = maxWidth
        val itemWidth = maxWidth / navItems.size

        // 1. 역동적인 Liquid 이동을 위한 애니메이션 상태 정의
        val animatedOffsetX by animateDpAsState(
            targetValue = itemWidth * selectedIndex,
            animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f), // 기존의 부드러운 수치로 복구
            label = "pill_move",
        )

        val pillExtraWidth by animateDpAsState(
            targetValue = 0.dp,
            animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
            label = "pill_stretch",
        )

        // Dp를 Px로 변환하기 위한 밀도(Density) 정보
        val density = LocalDensity.current
        val basePillWidth = itemWidth - 8.dp
        val lensHeightPx = with(density) { (navBarHeight - 8.dp).toPx() }
        val cornerRadiusPx = with(density) { 28.dp.toPx() }

        // 2. 배경 레이어 (Glassmorphism)
        Surface(
            shape = CircleShape,
            color = surfaceContainerColor.copy(alpha = 0.9f), // 투명도를 극대화하여 리스트 비침 효과 강조
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .cloudy(radius = 80),
        ) {}

        // 3. 선택 영역 캡슐 레이어: 테마 대응형 liquidGlass + 입체감 보정
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(x = animatedOffsetX.roundToPx(), y = 0)
                }
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .width(basePillWidth + pillExtraWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(28.dp))
                // 1) 내부 입체감 보정: 테마별 하이라이트 적용
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            highlightColor, // 테마에 최적화된 광원 색상
                            Color.Transparent,
                        ),
                        center = Offset(x = with(density) { (basePillWidth + pillExtraWidth).toPx() / 2 }, y = 0f),
                        radius = with(density) { (basePillWidth + pillExtraWidth).toPx() },
                    ),
                )
                // 2) 고도화된 셰이더 기반 볼록 렌즈 효과 (테마별 강도 조절)
                .liquidGlass(
                    lensCenter = Offset(
                        x = with(density) { (basePillWidth + pillExtraWidth).toPx() / 2 },
                        y = lensHeightPx / 2,
                    ),
                    lensSize = androidx.compose.ui.geometry.Size(
                        width = with(density) { (basePillWidth + pillExtraWidth).toPx() },
                        height = lensHeightPx,
                    ),
                    refraction = 0.45f,
                    curve = 0.85f,
                    cornerRadius = cornerRadiusPx,
                    tint = onSurfaceColor.copy(alpha = if (isLightMode) 0.05f else 0.15f), // 라이트 모드에선 더 투명하게
                    edge = if (isLightMode) 0.25f else 0.45f, // 다크 모드에서 반사광을 더 선명하게
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
