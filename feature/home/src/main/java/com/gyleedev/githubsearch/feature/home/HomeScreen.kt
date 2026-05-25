package com.gyleedev.githubsearch.feature.home

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.gyleedev.githubsearch.core.designsystem.theme.component.PulsingHeart
import com.gyleedev.githubsearch.core.designsystem.theme.component.UserAvatar
import com.gyleedev.githubsearch.core.designsystem.theme.component.UserInfoItem
import com.gyleedev.githubsearch.domain.model.FetchState
import com.gyleedev.githubsearch.domain.model.UpdateFavoriteResult
import com.gyleedev.githubsearch.domain.model.UserModel
import com.skydoves.cloudy.cloudy
import kotlinx.coroutines.flow.collectLatest
import com.gyleedev.githubsearch.core.designsystem.R as DesignSystemR
import com.gyleedev.githubsearch.feature.home.R as HomeR

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun HomeScreen(
    moveToDetail: (String) -> Unit,
    requestAuthentication: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userList = viewModel.users.collectAsLazyPagingItems()
    val unknownHostException = stringResource(id = DesignSystemR.string.unknown_host_exception)
    val socketException = stringResource(id = DesignSystemR.string.socket_exception)
    val httpException = stringResource(id = DesignSystemR.string.http_exception)
    val etcException = stringResource(id = DesignSystemR.string.etc_exception)
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val updateSuccess = stringResource(id = DesignSystemR.string.update_user_success)
    val updateFail = stringResource(id = DesignSystemR.string.update_user_fail)

    val currentLogins by remember(userList.itemSnapshotList) {
        derivedStateOf { userList.itemSnapshotList.items.map { it.login } }
    }

    val isAllSelected by remember {
        derivedStateOf {
            val state = uiState
            if (state is HomeUiState.Success) {
                currentLogins.isNotEmpty() && currentLogins.all { state.selectedUsers.contains(it) }
            } else {
                false
            }
        }
    }

    LaunchedEffect(viewModel.fetchState, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.fetchState.collectLatest { fetchState ->
                val message =
                    when (fetchState) {
                        FetchState.WRONG_CONNECTION -> unknownHostException
                        FetchState.BAD_INTERNET -> socketException
                        FetchState.PARSE_ERROR -> httpException
                        FetchState.FAIL -> etcException
                    }
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    LaunchedEffect(viewModel.showUpdateState, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.showUpdateState.collectLatest { result ->
                val message = if (result == UpdateFavoriteResult.Success) {
                    updateSuccess
                } else {
                    updateFail
                }
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    if (uiState is HomeUiState.Success) {
        val state = uiState as HomeUiState.Success
        HomeScreen(
            uiState = state,
            userList = userList,
            isAllSelected = isAllSelected,
            snackbarHostState = snackbarHostState,
            onSearch = viewModel::searchUser,
            onSearchItemReset = { viewModel.updateSearchId("") },
            onActiveChanged = viewModel::changeSearchBarState,
            onQueryChange = viewModel::updateSearchId,
            onToggleSelection = viewModel::changeSelectionState,
            onToggleAllSelection = {
                viewModel.selectCheckBox(currentLogins)
            },
            onClearSelection = viewModel::clearSelection,
            onAuthenticationDismiss = { viewModel.changeAuthDialogState(false) },
            onAuthenticationConfirm = {
                viewModel.changeAuthDialogState(false)
                requestAuthentication()
            },
            onDeleteRequest = { viewModel.changeDeleteDialogState(true) },
            onFavoriteRequest = viewModel::updateSelectedFavorite,
            onDeleteConfirm = viewModel::deleteSelectedUsers,
            onDeleteDismiss = { viewModel.changeDeleteDialogState(false) },
            moveToDetail = moveToDetail,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    uiState: HomeUiState.Success,
    userList: LazyPagingItems<UserModel>,
    isAllSelected: Boolean,
    snackbarHostState: SnackbarHostState,
    onSearch: (String) -> Unit,
    onSearchItemReset: () -> Unit,
    onActiveChanged: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
    onToggleSelection: (String) -> Unit,
    onToggleAllSelection: () -> Unit,
    onClearSelection: () -> Unit,
    onAuthenticationDismiss: () -> Unit,
    onAuthenticationConfirm: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onDeleteDismiss: () -> Unit,
    onDeleteRequest: () -> Unit,
    onFavoriteRequest: () -> Unit,
    moveToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            HomeTopAppBar(
                mode = uiState.mode,
                searchQuery = uiState.searchQuery,
                searchState = uiState.searchState,
                isLoading = uiState.isLoading,
                selectedCount = uiState.selectedUsers.size,
                isAllSelected = isAllSelected,
                onQueryChange = onQueryChange,
                onActiveChanged = onActiveChanged,
                onSearch = onSearch,
                onSearchItemReset = onSearchItemReset,
                onToggleAll = onToggleAllSelection,
                onClearSelection = onClearSelection,
                moveToDetail = moveToDetail,
            )
        },
        floatingActionButton = {
            HomeFloatingToolBar(
                isActive = uiState.selectedUsers.isNotEmpty(),
                onDeleteRequest = onDeleteRequest,
                onFavoriteRequest = onFavoriteRequest,
                // 전역 내비게이션 바(80dp) 위로 올리기 위해 하단 패딩 추가
                modifier = Modifier.padding(bottom = 80.dp),
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
            .fillMaxSize()
            // 시스템 내비게이션 바 영역 확보
            .navigationBarsPadding(),
    ) { paddingValues ->

        if (userList.itemCount > 0) {
            HomeItemList(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding()),
                users = userList,
                mode = uiState.mode,
                selectedUsers = uiState.selectedUsers,
                onToggleSelection = onToggleSelection,
                onClick = moveToDetail,
                // 인위적인 대형 공백 제거, 리스트 자체의 하단 여백은 최소화하여 "꽉 찬" 느낌 구현
                bottomPadding = 56.dp,
            )
        } else {
            NoItem(
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            )
        }

        if (uiState.showRequestAuthDialog) {
            AuthDialog(
                onConfirm = onAuthenticationConfirm,
                onDismiss = onAuthenticationDismiss,
            )
        }

        if (uiState.showDeleteDialog) {
            DeleteDialog(
                onConfirm = onDeleteConfirm,
                onDismiss = onDeleteDismiss,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    mode: HomeMode,
    searchQuery: String,
    searchState: SearchUiState,
    isLoading: Boolean,
    selectedCount: Int,
    isAllSelected: Boolean,
    onQueryChange: (String) -> Unit,
    onActiveChanged: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onSearchItemReset: () -> Unit,
    onToggleAll: () -> Unit,
    onClearSelection: () -> Unit,
    moveToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = mode,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "TopBarModeTransition",
        modifier = modifier,
    ) { currentMode ->
        when (currentMode) {
            HomeMode.SELECT -> {
                SelectionTopBar(
                    selectedCount = selectedCount,
                    isAllSelected = isAllSelected,
                    onToggleAll = onToggleAll,
                    onClearSelection = onClearSelection,
                )
            }

            HomeMode.DEFAULT, HomeMode.SEARCH -> {
                EmbeddedSearchBar(
                    onQueryChange = onQueryChange,
                    isSearchActive = currentMode == HomeMode.SEARCH,
                    query = searchQuery,
                    onActiveChanged = onActiveChanged,
                    onSearch = onSearch,
                    onSearchItemReset = onSearchItemReset,
                    moveToDetail = moveToDetail,
                    searchState = searchState,
                    loading = isLoading,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectionTopBar(
    selectedCount: Int,
    isAllSelected: Boolean,
    onToggleAll: () -> Unit,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = { Text(text = "${selectedCount}개 선택됨") },
        navigationIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { onToggleAll() },
            ) {
                Checkbox(
                    checked = isAllSelected,
                    onCheckedChange = null,
                )
                Text(
                    text = if (isAllSelected) "모두 선택 해제" else "모두 선택",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(start = 4.dp, end = 8.dp),
                )
            }
        },
        actions = {
            IconButton(onClick = onClearSelection) {
                Icon(imageVector = Icons.Rounded.Close, contentDescription = "Exit Selection")
            }
        },
        modifier = modifier,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EmbeddedSearchBar(
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    query: String,
    searchState: SearchUiState,
    onActiveChanged: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onSearchItemReset: () -> Unit,
    moveToDetail: (String) -> Unit,
    loading: Boolean,
    modifier: Modifier = Modifier,
) {
    val animatePadding by animateDpAsState(
        targetValue = if (isSearchActive) 0.dp else 24.dp,
        label = "animatePadding",
    )
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = isSearchActive,
                onExpandedChange = onActiveChanged,
                placeholder = {
                    Text(
                        text = "Search With Github Id",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                leadingIcon = {
                    if (isSearchActive) {
                        IconButton(
                            onClick = {
                                onActiveChanged(false)
                            },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                trailingIcon = {
                    if (isSearchActive && query.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onQueryChange("")
                                onSearchItemReset()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                },
            )
        },
        expanded = isSearchActive,
        onExpandedChange = onActiveChanged,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = animatePadding),
    ) {
        if (searchState is SearchUiState.Success) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                        .navigationBarsPadding()
                        .imePadding(),
            ) {
                SearchResultItem(
                    onClick = moveToDetail,
                    modifier = Modifier.align(Alignment.TopCenter),
                    login = searchState.login,
                    name = searchState.name,
                    bio = searchState.bio,
                    avatar = searchState.avatar,
                )

                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                Button(
                    onClick = { onSearch(query) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                ) {
                    Text("검색하세요")
                }
            }
        }
    }
}

@Composable
private fun AuthDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = DesignSystemR.string.title_request_authentication)) },
        text = { Text(text = stringResource(id = DesignSystemR.string.content_request_authentication)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(id = DesignSystemR.string.text_dialog_confirm),
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = DesignSystemR.string.text_dialog_cancel),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun DeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = DesignSystemR.string.title_request_delete)) },
        text = { Text(text = stringResource(id = DesignSystemR.string.content_request_delete)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = DesignSystemR.string.text_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = DesignSystemR.string.text_dialog_cancel),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun HomeItemList(
    users: LazyPagingItems<UserModel>,
    mode: HomeMode,
    selectedUsers: Set<String>,
    onToggleSelection: (String) -> Unit,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    bottomPadding: androidx.compose.ui.unit.Dp = 16.dp,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            top = 12.dp,
            // 마지막 아이템이 바 위로 살짝만 올라오도록 조정
            bottom = bottomPadding,
        ),
    ) {
        items(
            users.itemCount,
            key = { index -> users[index]?.login ?: "key_$index" },
        ) { index ->
            val user = users[index] as UserModel
            val isSelected = selectedUsers.contains(user.login)
            UserInfoItem(
                avatar = user.avatar,
                login = user.login,
                isSelected = isSelected,
                onClick = {
                    if (mode == HomeMode.SELECT) {
                        onToggleSelection(user.login)
                    } else {
                        onClick(user.login)
                    }
                },
                onLongClick = { onToggleSelection(user.login) },
            )
        }
    }
}

@Composable
private fun SearchResultItem(
    login: String,
    name: String?,
    bio: String?,
    avatar: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .clickable(onClick = { onClick(login) }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        UserAvatar(avatar = avatar)

        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
            if (name != null) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = login,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp),
            )

            if (bio != null) {
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeFloatingToolBar(
    isActive: Boolean,
    onDeleteRequest: () -> Unit,
    onFavoriteRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isActive) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                shape = CircleShape,
                shadowElevation = 10.dp,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .cloudy(radius = 80),
            ) {}

            Row(
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 12.dp,
                ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onFavoriteRequest) {
                    PulsingHeart()
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onDeleteRequest) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "삭제",
                    )
                }
            }
        }
    }
}

@Composable
private fun NoItem(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(HomeR.drawable.github_svgrepo_com),
            contentDescription = "Github Background Logo",
            tint = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.size(280.dp),
        )
    }
}
