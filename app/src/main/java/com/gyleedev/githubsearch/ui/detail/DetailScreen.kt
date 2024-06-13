package com.gyleedev.githubsearch.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gyleedev.githubsearch.R
import com.gyleedev.githubsearch.domain.model.DetailFeed
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel(),
    onClick: () -> Unit,
) {
    val list by viewModel.itemList.collectAsStateWithLifecycle()
    val status by viewModel.favoriteStatus.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            // TODO TopAppBar 앱 공통으로
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    IconButton(onClick = onClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.icon_content_description_arrow_back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::updateFavoriteStatus) {
                        if (status) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = stringResource(id = R.string.icon_content_description_favorite_filled),
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.FavoriteBorder,
                                contentDescription = stringResource(id = R.string.icon_content_description_favorite_bordered),
                            )
                        }
                    }
                },
                modifier = Modifier,
            )
        },
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(vertical = 4.dp),
        ) {
            items(list.size) { current ->
                when (list[current]) {
                    is DetailFeed.UserProfile -> {
                        DetailUserTitleItem(list[current] as DetailFeed.UserProfile)
                    }

                    is DetailFeed.UserDetail -> {
                        DetailUserInfoItem(user = list[current] as DetailFeed.UserDetail)
                    }

                    is DetailFeed.RepoTitle -> {
                        DetailRepoTitle()
                    }

                    is DetailFeed.RepoDetail -> {
                        DetailRepoItem(repos = list[current] as DetailFeed.RepoDetail)
                    }

                    else -> {
                        DetailRepoNoItem()
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailUserTitleItem(
    user: DetailFeed.UserProfile,
) {
    val data = user.userModel

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        GlideImage(
            imageModel = { data.avatar },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .heightIn(max = 80.dp, min = 20.dp)
                .widthIn(max = 80.dp, min = 20.dp)
                .clip(CircleShape),
            component = rememberImageComponent {
                +ShimmerPlugin(
                    Shimmer.Flash(
                        baseColor = Color.White,
                        highlightColor = Color.LightGray,
                    ),
                )
            },
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = data.repos.toString(),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(id = R.string.detail_user_title_repos),
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = data.followers.toString(),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(id = R.string.detail_user_title_follower),
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = data.following.toString(),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(id = R.string.detail_user_title_following),
            )
        }
    }
}

@Composable
private fun DetailUserInfoItem(user: DetailFeed.UserDetail) {
    val data = user.userModel

    Column(modifier = Modifier.padding(12.dp)) {
        data.name?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp),
            )
        }
        Text(
            text = data.login,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(12.dp),
        )
        data.bio?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(12.dp),
            )
        }

        if (data.company != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Apartment,
                    contentDescription = null,
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp),
                )
                Text(text = data.company)
            }
        }

        if (data.email != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Mail,
                    contentDescription = null,
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp),
                )
                Text(text = data.email)
            }
        }

        if (data.blogUrl != "") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Link,
                    contentDescription = null,
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp),
                )
                data.blogUrl?.let { Text(text = it) }
            }
        }
    }
}

@Composable
private fun DetailRepoTitle() {
    Text(
        text = stringResource(id = R.string.detail_repos_title_text),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
    )
}

@Composable
private fun DetailRepoItem(repos: DetailFeed.RepoDetail) {
    val data = repos.repositoryModel

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
        data.name?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
        data.description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(vertical = 4.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(24.dp)
                    .height(24.dp),
                colorResource(id = R.color.yellow),
            )
            Text(text = data.stargazer.toString(), modifier = Modifier.padding(8.dp))
            data.language?.let { Text(text = it, modifier = Modifier.padding(8.dp)) }
        }
    }
}

@Composable
private fun DetailRepoNoItem() {
    Text(
        text = stringResource(id = R.string.detail_repo_no_item),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        style = MaterialTheme.typography.titleMedium,
    )
}
