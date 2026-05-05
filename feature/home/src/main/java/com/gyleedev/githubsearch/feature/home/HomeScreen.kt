package com.gyleedev.githubsearch.feature.home

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.gyleedev.githubsearch.domain.model.FetchState
import com.gyleedev.githubsearch.domain.model.SearchStatus
import com.gyleedev.githubsearch.domain.model.UserModel
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import com.gyleedev.githubsearch.core.designsystem.R as DesignSystemR
import com.gyleedev.githubsearch.feature.home.R as HomeR

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun HomeScreen(
    moveToDetail: (String) -> Unit,
    requestAuthentication: () -> Unit,
    requestBottomBarStatus: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val users = viewModel.users.collectAsLazyPagingItems()

    val unknownHostException = stringResource(id = DesignSystemR.string.unknown_host_exception)
    val socketException = stringResource(id = DesignSystemR.string.socket_exception)
    val httpException = stringResource(id = DesignSystemR.string.http_exception)
    val etcException = stringResource(id = DesignSystemR.string.etc_exception)
    val noSuchUserMessage = stringResource(id = HomeR.string.search_result_no_user)
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.fetchState.collect { fetchState ->
            val message =
                when (fetchState) {
                    FetchState.WRONG_CONNECTION -> unknownHostException
                    FetchState.BAD_INTERNET -> socketException
                    FetchState.PARSE_ERROR -> httpException
                    FetchState.FAIL -> etcException
                }
            snackBarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.errorAlert.collect { status ->
            when (status) {
                SearchStatus.NO_SUCH_USER -> {
                    snackBarHostState.showSnackbar(
                        message = noSuchUserMessage,
                        duration = SnackbarDuration.Short
                    )
                }

                SearchStatus.BAD_NETWORK -> {
                    snackBarHostState.showSnackbar(
                        message = httpException,
                        duration = SnackbarDuration.Short
                    )
                }

                else -> {
                    println("no information error $status")
                }
            }
        }
    }
    var showRequestAuthenticationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.requestAuthentication.collect {
            showRequestAuthenticationDialog = true
        }
    }

    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isSearchActive) {
        requestBottomBarStatus(isSearchActive)
    }
    if (uiState is HomeUiState.Success) {
        Scaffold(
            topBar = {
                EmbeddedSearchBar(
                    onQueryChange = viewModel::updateSearchId,
                    isSearchActive = isSearchActive,
                    query = (uiState as HomeUiState.Success).searchQuery,
                    onActiveChanged = { isSearchActive = it },
                    onSearch = viewModel::getUser,
                    onSearchItemReset = viewModel::resetQuery,
                    moveToDetail = { (uiState as HomeUiState.Success).searchedUser?.let { moveToDetail(it.login) } },
                    user = (uiState as HomeUiState.Success).searchedUser,
                    loading = (uiState as HomeUiState.Success).isLoading,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            modifier = modifier.fillMaxSize(),
        ) { paddingValues ->

            if (users.itemCount > 0) {
                SearchItemList(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    users = users,
                    onClick = { moveToDetail(it) },
                )
            } else {
                NoItem(
                    modifier = Modifier.padding(paddingValues),
                )
            }

            if (showRequestAuthenticationDialog) {
                AlertDialog(
                    onDismissRequest = { showRequestAuthenticationDialog = false },
                    title = { Text(text = stringResource(id = DesignSystemR.string.title_request_authentication)) },
                    text = { Text(text = stringResource(id = DesignSystemR.string.content_request_authentication)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                showRequestAuthenticationDialog = false
                                requestAuthentication()
                            },
                        ) {
                            Text(stringResource(id = DesignSystemR.string.text_dialog_confirm))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showRequestAuthenticationDialog = false
                            },
                        ) {
                            Text(stringResource(id = DesignSystemR.string.text_dialog_cancel))
                        }
                    },
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EmbeddedSearchBar(
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    query: String,
    onActiveChanged: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onSearchItemReset: () -> Unit,
    moveToDetail: () -> Unit,
    user: UserModel?,
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
        windowInsets = if (isSearchActive) WindowInsets(0, 0, 0, 0) else SearchBarDefaults.windowInsets,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = animatePadding),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .navigationBarsPadding()
                    .imePadding(),
        ) {
            if (user != null) {
                SearchResultItem(
                    user = user,
                    onClick = moveToDetail,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .align(Alignment.TopCenter),
                )
            }
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            Button(
                onClick = { onSearch(query) },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Text("검색하세요")
            }
        }
    }

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
            key = { users[it]?.login!! },
            contentType = { 0 },
        ) { index ->
            val user = users[index] as UserModel
            HomeItem(user, onClick = { onClick(user.login) })
        }
    }
}

@Composable
private fun HomeItem(
    user: UserModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp, max = 100.dp)
                .clickable(onClick = onClick)
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GlideImage(
            imageModel = { user.avatar },
            modifier =
                Modifier
                    .padding(horizontal = 8.dp)
                    .sizeIn(minWidth = 20.dp, minHeight = 20.dp, maxWidth = 80.dp, maxHeight = 80.dp)
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
        Text(
            text = user.login,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SearchResultItem(
    user: UserModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .clickable(onClick = onClick),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GlideImage(
            imageModel = { user.avatar },
            modifier =
                Modifier
                    .padding(horizontal = 8.dp)
                    .size(80.dp)
                    .clip(
                        CircleShape,
                    ),
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

        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
            val name = user.name
            if (name != null) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = user.login,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp),
            )

            val bio = user.bio
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
