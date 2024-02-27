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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun FavoriteScreen(
    moveToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel(),
) {
    val users = viewModel.getUsers().collectAsLazyPagingItems()

    Scaffold(
        topBar = {

        },
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) { paddingValues ->


        if (users.itemCount > 0) {
            FavoriteItemList(
                modifier = Modifier.padding(paddingValues),
                users = users,
                onClick = { moveToDetail(it) }
            )
        } else {
            NoItem(
                modifier = modifier.padding(paddingValues)
            )
        }


    }
}

@Composable
private fun FavoriteItemList(
    users: LazyPagingItems<UserModel>,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 12.dp)
    ) {
        items(users.itemCount, key = null, contentType = {}) { user ->
            users[user]?.let {
                FavoriteItem(it, onClick = { onClick(it.login) }, onLongClick = { println("longclick")})
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
private fun FavoriteItem(
    user: UserModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.combinedClickable(onLongClick = onLongClick, onClick = onClick),
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
