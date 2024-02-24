package com.gyleedev.githubsearch.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.gyleedev.githubsearch.domain.model.UserModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    moveToDetail: (String) -> Unit,
) {
    val users = viewModel.getUsers().collectAsLazyPagingItems()
    /*val user = viewModel.userInfo.collectAsStateWithLifecycle()
    val state = viewModel.state.collectAsStateWithLifecycle()*/
    var searchText by remember { mutableStateOf("") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            EmbeddedSearchBar(
                onQueryChange = { searchText = it
                                println(it)},
                isSearchActive = isSearchActive,
                onActiveChanged = { isSearchActive = it },
            )

        },
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) { paddingValues ->

        if (users.itemCount > 0) {
            SearchItemList(
                modifier = modifier.padding(paddingValues),
                users = users,
                onClick = {}//onScreenChange
            )

        } else {

        }

    }
}

/*@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    onArrowBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {

    }
    TextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        leadingIcon = {
            if (searchText == "") {
                SearchBarIcon(
                    imageVector = Icons.Default.Search,
                    modifier = Modifier,
                    onClick = {}
                )
            } else {
                SearchBarIcon(
                    onClick = onArrowBackClicked,
                    imageVector = Icons.Default.ArrowBack,
                    modifier = Modifier
                )
            }
        },
        trailingIcon = {
            if (searchText != "") {
                Row {
                    SearchBarIcon(
                        onClick = onCloseClicked,
                        imageVector = Icons.Default.Close,
                        modifier = Modifier
                    )
                    SearchBarIcon(
                        onClick = onSearchClicked,
                        imageVector = Icons.Default.Search,
                        modifier = Modifier
                    )
                }
            }

        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        placeholder = {
            Text(text = "search")
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .onFocusChanged {
                println("focus $it, ${it.isFocused}")
            },
        singleLine = true
    )
}*/

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EmbeddedSearchBar(
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: ((String) -> Unit)? = null,
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
        onSearch = onSearch ?: { activeChanged(false) },
        active = isSearchActive,
        onActiveChange = activeChanged,
        modifier = if (isSearchActive) {
            modifier
                .animateContentSize(spring(stiffness = Spring.StiffnessHigh))
                .onFocusChanged {
                    println("aa : $it")
                }
        } else {
            modifier
                .padding(start = 12.dp, top = 2.dp, end = 12.dp, bottom = 12.dp)
                .fillMaxWidth()
                .animateContentSize(spring(stiffness = Spring.StiffnessHigh))
                .onFocusChanged {
                    println("bb : $it")
                }
        },
        placeholder = { Text("Search") },
        leadingIcon = {
            if (isSearchActive) {
                IconButton(
                    onClick = { activeChanged(false) },
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
        // Search suggestions or results
    }
}


@Composable
private fun SearchBarIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null
        )
    }

}

@Composable
private fun SearchItemList(
    users: LazyPagingItems<UserModel>,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
    ) {
        items(users.itemCount, key = null, contentType = {}) { user ->
            users[user]?.let {
                HomeItem(it, onClick = { onClick(it.login) })
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun HomeItem(
    user: UserModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        onClick = onClick
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
                    .heightIn(max = 80.dp)
                    .widthIn(max = 80.dp)
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun SearchResultItem(
    user: UserModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlideImage(
                model = (user.avatar),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .heightIn(max = 80.dp)
                    .widthIn(max = 80.dp)
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