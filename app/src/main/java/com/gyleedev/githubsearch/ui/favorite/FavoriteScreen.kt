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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.gyleedev.githubsearch.domain.model.UserModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    moveToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel(),
) {
    val users = viewModel.user.collectAsLazyPagingItems()
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
                title = { Text(text = "Favorite") },
                actions = {
                    IconButton(onClick = { showFilterDialog.value = !showFilterDialog.value }) {

                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "favorite button",
                        )


                    }
                },
                modifier = Modifier,
            )

        },
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) { paddingValues ->


        if (users.itemCount > 0) {
            FavoriteItemList(
                modifier = Modifier.padding(paddingValues),
                users = users,
                onClick = { moveToDetail(it) },
                onLongClick = {
                    user.value = it
                    showDeleteDialog.value = true
                }
            )
        } else {
            NoItem(
                modifier = modifier.padding(paddingValues)
            )
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text(text = "즐겨찾기를 해제하겠습니까?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog.value = false
                        user.value?.let { viewModel.updateFavoriteStatus(it) }
                    }) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteDialog.value = false
                        user.value = null
                    }) {
                    Text("취소")
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
            onSelectedItemChange = { selectedItem.intValue = it })
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
        items(users.itemCount, key = null, contentType = {}) { user ->
            users[user]?.let { it ->
                FavoriteItem(it, onClick = { onClick(it.login) }, onLongClick = { onLongClick(it) })
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
private fun FavoriteItem(
    user: UserModel,
    onClick: () -> Unit,
    onLongClick: (UserModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.combinedClickable(
            onLongClick = { onLongClick(user) },
            onClick = onClick
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp, max = 100.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlideImage(
                model = (user.avatar),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .heightIn(max = 80.dp, min = 20.dp)
                    .widthIn(max = 80.dp, min = 20.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Text(
                text = user.login,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Composable
private fun NoItem(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),

        ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No Saved Item",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
        }

    }
}

@Composable
fun FilterDialog(
    selectedItemId: Int,
    onChangeState: (Boolean) -> Unit,
    onFilterChange: () -> Unit,
    onSelectedItemChange: (Int) -> Unit
) {

    val declarations = listOf("공개 repository가 있는 유저", "공개 repository가 없는 유저", "전체")



    AlertDialog(

        // 다이얼로그 뷰 밖의 화면 클릭시, 인자로 받은 함수 실행하며 다이얼로그 상태 변경
        onDismissRequest = { onChangeState(false) },
        title = {
            Text(
                text = "필터",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column {
                Text(text = "조건을 선택하세요", modifier = Modifier.padding(bottom = 5.dp))
                RadioButtons(
                    selectedItemId = selectedItemId,
                    declarations,
                    selectedItem = { string ->
                        declarations.indexOf(string).also { onSelectedItemChange(it) }
                    })
            }
        },
        dismissButton = {
            TextButton(onClick = { onChangeState(false) }) {
                Text(text = "취소", color = Color.Black)
            }
        },
        confirmButton = {

            TextButton(
                onClick = {
                    onFilterChange()
                    onChangeState(false)
                }) {
                Text(text = "확인", color = Color.Black)
            }
        }
    )

}

// 다이얼로그에 들어가는 라디오버튼
@Composable
fun RadioButtons(selectedItemId: Int, declaration: List<String>, selectedItem: (String) -> Unit) {
    val selectedValue = remember { mutableStateOf("") } // 선택된 라디오 버튼에 해당하는 내용
    val isSelectedItem: (String) -> Boolean = { selectedValue.value == it }
    val onChangeState: (String) -> Unit = {
        selectedValue.value = it
        selectedItem(selectedValue.value)
    }

    Column(modifier = Modifier.padding(top = 10.dp)) {
        declaration.forEach { item ->
            Column {
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = isSelectedItem(item),
                            onClick = { onChangeState(item) },
                            role = Role.RadioButton
                        )
                        .padding(bottom = 3.dp)
                ) {
                    RadioButton(
                        selected = declaration[selectedItemId] == item,
                        onClick = null,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(text = item)
                }
            }
        }
    }
}