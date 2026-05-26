package com.gyleedev.githubsearch.feature.favorite

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.gyleedev.githubsearch.core.designsystem.component.LiquidNavBarDefaults
import com.gyleedev.githubsearch.core.designsystem.theme.component.UserInfoItem
import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.core.designsystem.R as DesignSystemR
import com.gyleedev.githubsearch.feature.favorite.R as FavoriteR

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    moveToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel(),
) {
    val users = viewModel.items.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { FavoriteTopAppBar(onClick = viewModel::updateShowFilterDialog) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
            .fillMaxSize(),
    ) { paddingValues ->
        if (uiState is FavoriteUiState.Success) {
            val state = uiState as FavoriteUiState.Success
            FavoriteScreen(
                users = users,
                filterStatus = state.filterState,
                onFilterDismiss = viewModel::updateShowFilterDialog,
                onFilterClick = viewModel::updateFilter,
                onFavoriteDelete = viewModel::updateFavoriteStatus,
                onDeleteCancel = viewModel::updateShowFavoriteDialog,
                onItemClick = moveToDetail,
                onItemLongClick = viewModel::showFavoriteDialog,
                showFavoriteDialog = state.favoriteDialogState,
                showFilterDialog = state.filterDialogState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteScreen(
    users: LazyPagingItems<UserModel>,
    filterStatus: FilterStatus,
    onFilterDismiss: () -> Unit,
    onFilterClick: (FilterStatus) -> Unit,
    onFavoriteDelete: () -> Unit,
    onDeleteCancel: () -> Unit,
    onItemClick: (String) -> Unit,
    onItemLongClick: (UserModel) -> Unit,
    showFavoriteDialog: Boolean,
    showFilterDialog: Boolean,
    modifier: Modifier = Modifier,
) {
    if (users.itemCount > 0) {
        FavoriteItemList(
            modifier = modifier,
            users = users,
            onClick = onItemClick,
            onLongClick = onItemLongClick,
        )
    } else {
        NoItem(modifier = modifier)
    }

    if (showFavoriteDialog) {
        DeleteFavoriteDialog(
            onConfirm = onFavoriteDelete,
            onDismiss = onDeleteCancel,
        )
    }
    if (showFilterDialog) {
        FilterDialog(
            onDismiss = onFilterDismiss,
            selectedFilter = filterStatus,
            onSelectedItemChange = onFilterClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteTopAppBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(text = stringResource(id = FavoriteR.string.title_favorite)) },
        actions = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = stringResource(id = FavoriteR.string.icon_content_description_filter),
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun FavoriteItemList(
    users: LazyPagingItems<UserModel>,
    onClick: (String) -> Unit,
    onLongClick: (UserModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            top = 12.dp,
        ),
    ) {
        items(
            users.itemCount,
            key = { index ->
                users[index]?.login ?: "placeholder_$index"
            },
        ) { index ->
            users[index]?.let { user ->
                UserInfoItem(
                    onClick = { onClick(user.login) },
                    onLongClick = { onLongClick(user) },
                    avatar = user.avatar,
                    name = user.name,
                    login = user.login,
                    follower = user.followers,
                    company = user.company,
                )
            }
        }

        // 마지막 아이템이 하단 내비게이션 바에 가리지 않도록 빈 공간 추가
        // 시스템 바 영역 + LiquidNavBar + 여유 확보
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = LiquidNavBarDefaults.Height + LiquidNavBarDefaults.BottomMargin),
            )
        }
    }
}

@Composable
private fun NoItem(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(id = FavoriteR.string.favorite_no_item),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun DeleteFavoriteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = FavoriteR.string.text_delete_favorite_title)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
            ) {
                Text(stringResource(id = DesignSystemR.string.text_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(stringResource(id = DesignSystemR.string.text_dialog_cancel))
            }
        },
        modifier = modifier,
    )
}

@Composable
fun FilterDialog(
    selectedFilter: FilterStatus,
    onDismiss: () -> Unit,
    onSelectedItemChange: (FilterStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = DesignSystemR.string.text_filter_title),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Column {
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedFilter == FilterStatus.ALL,
                            onClick = { onSelectedItemChange(FilterStatus.ALL) },
                            role = Role.RadioButton,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedFilter == FilterStatus.ALL,
                        onClick = { onSelectedItemChange(FilterStatus.ALL) },
                        modifier = Modifier.padding(end = 5.dp),
                    )
                    Text(text = stringResource(FavoriteR.string.filter_list_all))
                }
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedFilter == FilterStatus.REPO,
                            onClick = { onSelectedItemChange(FilterStatus.REPO) },
                            role = Role.RadioButton,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedFilter == FilterStatus.REPO,
                        onClick = { onSelectedItemChange(FilterStatus.REPO) },
                        modifier = Modifier.padding(end = 5.dp),
                    )
                    Text(text = stringResource(FavoriteR.string.filter_list_has_repos))
                }
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedFilter == FilterStatus.NOREPO,
                            onClick = { onSelectedItemChange(FilterStatus.NOREPO) },
                            role = Role.RadioButton,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedFilter == FilterStatus.NOREPO,
                        onClick = { onSelectedItemChange(FilterStatus.NOREPO) },
                        modifier = Modifier.padding(end = 5.dp),
                    )
                    Text(text = stringResource(FavoriteR.string.filter_list_no_repos))
                }
            }
        },
        modifier = modifier,
        dismissButton = {},
        confirmButton = {},
    )
}
