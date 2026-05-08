package com.gyleedev.githubsearch.feature.favorite

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.gyleedev.githubsearch.core.designsystem.theme.component.UserAvatar
import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.core.designsystem.R as DesignSystemR
import com.gyleedev.githubsearch.feature.favorite.R as FavoriteR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    moveToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel(),
) {
    val users = viewModel.items.collectAsLazyPagingItems()
    val selectedFilter by viewModel.filterState.collectAsStateWithLifecycle()
    val showDeleteDialog = remember { mutableStateOf(false) }
    val showFilterDialog = remember { mutableStateOf(false) }
    // TODO 이걸 왜 focus 하는지 확인하고 로직 바꿀 것.
    val user = remember { mutableStateOf<UserModel?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = FavoriteR.string.title_favorite)) },
                actions = {
                    IconButton(onClick = { showFilterDialog.value = !showFilterDialog.value }) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = stringResource(id = FavoriteR.string.icon_content_description_filter),
                        )
                    }
                },
                modifier = Modifier,
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        if (users.itemCount > 0) {
            FavoriteItemList(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(paddingValues),
                users = users,
                onClick = { moveToDetail(it) },
                onLongClick = {
                    user.value = it
                    showDeleteDialog.value = true
                },
            )
        } else {
            NoItem(
                modifier = Modifier.padding(paddingValues),
            )
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text(text = stringResource(id = FavoriteR.string.text_delete_favorite_title)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog.value = false
                        user.value?.let { viewModel.updateFavoriteStatus(it) }
                    },
                ) {
                    Text(stringResource(id = DesignSystemR.string.text_dialog_confirm))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteDialog.value = false
                        user.value = null
                    },
                ) {
                    Text(stringResource(id = DesignSystemR.string.text_dialog_cancel))
                }
            },
        )
    }

    if (showFilterDialog.value) {
        FilterDialog(
            onChangeState = { showFilterDialog.value = it },
            selectedFilter = selectedFilter,
            onSelectedItemChange = viewModel::updateFilter,
        )
    }
}

@Composable
private fun FavoriteItemList(
    users: LazyPagingItems<UserModel>,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    onLongClick: (UserModel) -> Unit,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
    ) {
        items(
            users.itemCount,
            key = { index ->
                users[index]?.login ?: "placeholder_$index"
            },
        ) { index ->
            users[index]?.let { user ->
                FavoriteItem(
                    user = user,
                    onClick = { onClick(user.login) },
                    onLongClick = { onLongClick(it) },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FavoriteItem(
    user: UserModel,
    onClick: () -> Unit,
    onLongClick: (UserModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp, max = 100.dp)
                .padding(12.dp)
                .combinedClickable(
                    onLongClick = { onLongClick(user) },
                    onClick = onClick,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        UserAvatar(avatar = user.avatar)
        Text(
            text = user.login,
            fontWeight = FontWeight.Bold,
        )
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
fun FilterDialog(
    selectedFilter: FilterStatus,
    onChangeState: (Boolean) -> Unit,
    onSelectedItemChange: (FilterStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = { onChangeState(false) },
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
