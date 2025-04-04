package com.gyleedev.githubsearch.ui.favorite

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.gyleedev.githubsearch.R
import com.gyleedev.githubsearch.domain.model.UserModel
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    moveToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val users = viewModel.items.collectAsLazyPagingItems()
    users.refresh()
    val showDeleteDialog = remember { mutableStateOf(false) }
    val showFilterDialog = remember { mutableStateOf(false) }
    val user = remember {
        mutableStateOf<UserModel?>(null)
    }
    val selectedItem = remember {
        mutableIntStateOf(2)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.title_favorite)) },
                actions = {
                    IconButton(onClick = { showFilterDialog.value = !showFilterDialog.value }) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = stringResource(id = R.string.icon_content_description_filter)
                        )
                    }
                },
                modifier = Modifier
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        if (users.itemCount > 0) {
            FavoriteItemList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                users = users,
                onClick = { moveToDetail(it) },
                onLongClick = {
                    user.value = it
                    showDeleteDialog.value = true
                }
            )
        } else {
            NoItem(
                modifier = Modifier.padding(paddingValues)
            )
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text(text = stringResource(id = R.string.text_delete_favorite_title)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog.value = false
                        user.value?.let { viewModel.updateFavoriteStatus(it) }
                    }
                ) {
                    Text(stringResource(id = R.string.text_dialog_confirm))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteDialog.value = false
                        user.value = null
                    }
                ) {
                    Text(stringResource(id = R.string.text_dialog_cancel))
                }
            }
        )
    }

    if (showFilterDialog.value) {
        FilterDialog(
            onChangeState = { showFilterDialog.value = it },
            onFilterChange = {
                when (selectedItem.intValue) {
                    0 -> {
                        viewModel.userFilterHasRepos()
                    }

                    1 -> {
                        viewModel.userFilterNoRepos()
                    }

                    2 -> {
                        viewModel.userFilterAll()
                    }
                }
            },
            selectedItemId = selectedItem.intValue,
            onSelectedItemChange = { selectedItem.intValue = it }
        )
    }
}

@Composable
private fun FavoriteItemList(
    users: LazyPagingItems<UserModel>,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    onLongClick: (UserModel) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 12.dp)
    ) {
        items(users.itemCount, key = { users[it]!!.login }, contentType = { 0 }) { index ->
            val user = users[index] as UserModel
            FavoriteItem(
                user,
                onClick = { onClick(user.login) },
                onLongClick = { onLongClick(it) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FavoriteItem(
    user: UserModel,
    onClick: () -> Unit,
    onLongClick: (UserModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp, max = 100.dp)
            .padding(12.dp)
            .combinedClickable(
                onLongClick = { onLongClick(user) },
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)

    ) {
        GlideImage(
            imageModel = { user.avatar },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .heightIn(max = 80.dp, min = 20.dp)
                .widthIn(max = 80.dp, min = 20.dp)
                .clip(CircleShape),
            component = rememberImageComponent {
                +ShimmerPlugin(
                    Shimmer.Flash(
                        baseColor = Color.White,
                        highlightColor = Color.LightGray
                    )
                )
            }
        )
        Text(
            text = user.login,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun NoItem(
    modifier: Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.favorite_no_item),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FilterDialog(
    selectedItemId: Int,
    onChangeState: (Boolean) -> Unit,
    onFilterChange: () -> Unit,
    onSelectedItemChange: (Int) -> Unit
) {
    val declarations = listOf(
        stringResource(id = R.string.filter_list_has_repos),
        stringResource(id = R.string.filter_list_no_repos),
        stringResource(id = R.string.filter_list_all)
    )

    AlertDialog(
        onDismissRequest = { onChangeState(false) },
        title = {
            Text(
                text = stringResource(id = R.string.text_filter_title),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(id = R.string.text_filter_content),
                    modifier = Modifier.padding(bottom = 5.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                RadioButtons(
                    selectedItemId = selectedItemId,
                    RadioItems(declarations),
                    selectedItem = { string ->
                        declarations.indexOf(string).also { onSelectedItemChange(it) }
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onChangeState(false) }) {
                Text(text = stringResource(id = R.string.text_filter_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onFilterChange()
                    onChangeState(false)
                }
            ) {
                Text(text = stringResource(id = R.string.text_filter_confirm))
            }
        }
    )
}

@Composable
fun RadioButtons(
    selectedItemId: Int,
    items: RadioItems,
    selectedItem: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedValue = remember { mutableStateOf("") }
    val isSelectedItem: (String) -> Boolean = { selectedValue.value == it }
    val onChangeState: (String) -> Unit = {
        selectedValue.value = it
        selectedItem(selectedValue.value)
    }

    Column(modifier = modifier.padding(top = 10.dp)) {
        val declaration = items.list
        declaration.forEach { item ->
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .selectable(
                            selected = isSelectedItem(item),
                            onClick = { onChangeState(item) },
                            role = Role.RadioButton
                        )
                        .padding(bottom = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = declaration[selectedItemId] == item,
                        onClick = null,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(text = item, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

data class RadioItems(
    val list: List<String>
)
