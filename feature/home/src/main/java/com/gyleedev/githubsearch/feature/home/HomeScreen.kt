package com.gyleedev.githubsearch.feature.home

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.gyleedev.githubsearch.core.designsystem.theme.component.UserAvatar
import com.gyleedev.githubsearch.core.designsystem.theme.component.UserInfoItem
import com.gyleedev.githubsearch.domain.model.FetchState
import com.gyleedev.githubsearch.domain.model.SearchStatus
import com.gyleedev.githubsearch.domain.model.UserModel
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
    val noSuchUserMessage = stringResource(id = HomeR.string.search_result_no_user)
    val snackBarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

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
                snackBarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    LaunchedEffect(viewModel.errorAlert, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.errorAlert.collectLatest { status ->
                when (status) {
                    SearchStatus.NO_SUCH_USER -> {
                        snackBarHostState.showSnackbar(
                            message = noSuchUserMessage,
                            duration = SnackbarDuration.Short,
                        )
                    }

                    SearchStatus.BAD_NETWORK -> {
                        snackBarHostState.showSnackbar(
                            message = httpException,
                            duration = SnackbarDuration.Short,
                        )
                    }

                    else -> {
                        println("no information error $status")
                    }
                }
            }
        }
    }

    if (uiState is HomeUiState.Success) {
        val state = uiState as HomeUiState.Success
        HomeScreen(
            uiState = state,
            onSearch = viewModel::searchUser,
            onSearchItemReset = { viewModel.updateSearchId("") },
            onActiveChanged = viewModel::changeSearchBarState,
            onQueryChange = viewModel::updateSearchId,
            moveToDetail = moveToDetail,
            userList = userList,
            snackbarHostState = snackBarHostState,
            onDismiss = { viewModel.changeDialogState(false) },
            onConfirm = {
                viewModel.changeDialogState(false)
                requestAuthentication()
            },
            modifier = modifier,
        )
    }
}

@Composable
internal fun HomeScreen(
    uiState: HomeUiState.Success,
    userList: LazyPagingItems<UserModel>,
    snackbarHostState: SnackbarHostState,
    onSearch: (String) -> Unit,
    onSearchItemReset: () -> Unit,
    onActiveChanged: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    moveToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            EmbeddedSearchBar(
                onQueryChange = onQueryChange,
                isSearchActive = uiState.isSearchActive,
                query = uiState.searchQuery,
                onActiveChanged = onActiveChanged,
                onSearch = onSearch,
                onSearchItemReset = onSearchItemReset,
                moveToDetail = moveToDetail,
                searchState = uiState.searchState,
                loading = uiState.isLoading,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->

        if (userList.itemCount > 0) {
            SearchItemList(
                modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                users = userList,
                onClick = { moveToDetail(it) },
            )
        } else {
            NoItem(
                modifier = Modifier.padding(paddingValues),
            )
        }

        if (uiState.showRequestAuthDialog) {
            AuthDialog(
                onConfirm = onConfirm,
                onDismiss = onDismiss,
            )
        }
    }
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
                placeholder = { Text(text = "search") },
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
            Button(
                onClick = onConfirm,
            ) {
                Text(stringResource(id = DesignSystemR.string.text_dialog_confirm))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
            ) {
                Text(stringResource(id = DesignSystemR.string.text_dialog_cancel))
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun SearchItemList(
    users: LazyPagingItems<UserModel>,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    LazyColumn(
        modifier =
        modifier
            .fillMaxSize()
            .padding(vertical = 12.dp),
    ) {
        items(
            users.itemCount,
            key = { index -> users[index]?.login ?: "key_$index" },
        ) { index ->
            val user = users[index] as UserModel
            UserInfoItem(
                avatar = user.avatar,
                login = user.login,
                onClick = { onClick(user.login) },
                onLongClick = {},
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

@Composable
private fun NoItem(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(id = HomeR.string.home_no_item),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}
