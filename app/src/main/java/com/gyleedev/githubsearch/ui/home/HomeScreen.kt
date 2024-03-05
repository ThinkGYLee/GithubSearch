package com.gyleedev.githubsearch.ui.home

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.gyleedev.githubsearch.R
import com.gyleedev.githubsearch.domain.model.UserModel

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun HomeScreen(
    moveToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val users = viewModel.users.collectAsLazyPagingItems()
    users.refresh()
    val user by viewModel.userInfo.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchState.collect { fetchState ->
            viewModel.changeLoadingState()
            Toast.makeText(context, "$fetchState", Toast.LENGTH_SHORT).show()
        }
    }


    var searchText by remember { mutableStateOf("") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            EmbeddedSearchBar(
                onQueryChange = {
                    searchText = it
                    viewModel.updateSearchId(it)
                },
                isSearchActive = isSearchActive,
                onActiveChanged = { isSearchActive = it },
                onSearch = {
                    searchText = ""
                    viewModel.getUser()
                },
                onSearchItemReset = { viewModel.resetUser() },
                moveToDetail = { user?.let { moveToDetail(it.login) } },
                user = user,
                loading = loading,
                modifier = Modifier
            )
        }, modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) { paddingValues ->
        when (users.loadState.refresh) {
            is LoadState.Loading -> {
                Surface(
                    modifier = modifier.fillMaxSize(),
                ) {
                    Box(
                        modifier = Modifier, Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier)
                    }
                }
            }

            is LoadState.Error -> {
                NoItem(
                    modifier = modifier.padding(paddingValues)
                )
            }

            else -> {
                if (users.itemCount > 0) {
                    SearchItemList(modifier = modifier.padding(paddingValues),
                        users = users,
                        onClick = { moveToDetail(it) })
                } else {
                    NoItem(
                        modifier = modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EmbeddedSearchBar(
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onSearchItemReset: () -> Unit,
    moveToDetail: () -> Unit,
    user: UserModel?,
    loading: Boolean,
    modifier: Modifier = Modifier,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val activeChanged: (Boolean) -> Unit = { active ->
        searchQuery = ""
        onQueryChange("")
        onActiveChanged(active)
    }

    SearchBar(
        query = searchQuery,
        onQueryChange = { query ->
            searchQuery = query
            onQueryChange(query)
        },
        onSearch = onSearch,
        active = isSearchActive,
        onActiveChange = activeChanged,
        modifier = modifier,
        placeholder = { Text(stringResource(id = R.string.placeholder_searchbar)) },
        leadingIcon = {
            if (isSearchActive) {
                IconButton(
                    onClick = {
                        searchQuery = ""
                        onQueryChange("")
                        activeChanged(false)
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
        trailingIcon = if (isSearchActive && searchQuery.isNotEmpty()) {
            {
                IconButton(
                    onClick = {
                        searchQuery = ""
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
        } else {
            null
        },
        colors = SearchBarDefaults.colors(
            containerColor = if (isSearchActive) {
                MaterialTheme.colorScheme.background
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            },
        ),
        tonalElevation = 0.dp,
        windowInsets = if (isSearchActive) {
            SearchBarDefaults.windowInsets
        } else {
            WindowInsets(0.dp)
        }
    ) {
        SearchResultItem(
            user = user, onClick = moveToDetail, modifier = Modifier
        )

        if (loading) {
            Surface(
                modifier = modifier.fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier, Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier)
                }
            }
        }
    }
}

@Composable
private fun SearchItemList(
    users: LazyPagingItems<UserModel>, modifier: Modifier = Modifier, onClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 12.dp)
    ) {
        items(
            users.itemCount,
            key = { users[it]?.login!! },
            contentType = { 0 }
        ) { index ->
            val user = users[index] as UserModel
            HomeItem(user, onClick = { onClick(user.login) })
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun HomeItem(
    user: UserModel, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp, max = 100.dp)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GlideImage(
            model = (user.avatar),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .sizeIn(minWidth = 20.dp, minHeight = 20.dp, maxWidth = 80.dp, maxHeight = 80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Text(
            text = user.login, fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun SearchResultItem(
    user: UserModel?, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    if (user != null) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .padding(12.dp)
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlideImage(
                model = (user.avatar),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(80.dp)
                    .clip(
                        CircleShape
                    ),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )

            Column {
                if (user.name != null) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = user.login,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                if (user.bio != null) {
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun NoItem(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.home_no_item),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}