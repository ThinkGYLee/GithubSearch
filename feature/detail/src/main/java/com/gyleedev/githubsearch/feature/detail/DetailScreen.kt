package com.gyleedev.githubsearch.feature.detail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.feature.detail.component.DetailRepoInfo
import com.gyleedev.githubsearch.feature.detail.component.DetailUserInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val repoList by viewModel.repo.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DetailAppBar(
                onBackClick = onBackClick,
                onFavoriteClick = viewModel::updateFavoriteStatus,
                favorite = user?.favorite,
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        if (user != null) {
            with(user as UserModel) {
                DetailScreen(
                    avatar = avatar,
                    repoCount = repoCount,
                    followers = followers,
                    following = following,
                    name = name,
                    login = login,
                    bio = bio,
                    company = company,
                    email = email,
                    blogUrl = blogUrl,
                    repoList = repoList,
                    paddingValues = paddingValues,
                )
            }
        }
    }
}

@Composable
private fun DetailScreen(
    avatar: String,
    repoCount: Int,
    followers: Int,
    following: Int,
    name: String?,
    login: String,
    bio: String?,
    company: String?,
    email: String?,
    blogUrl: String?,
    repoList: List<RepositoryModel>,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
        modifier
            .fillMaxSize()
            .padding(vertical = 4.dp),
        contentPadding = paddingValues,
    ) {
        item {
            DetailUserInfo(
                avatar = avatar,
                repos = repoCount,
                followers = followers,
                following = following,
                name = name,
                login = login,
                bio = bio,
                company = company,
                email = email,
                blogUrl = blogUrl,
            )
        }

        item {
            DetailRepoTitle()
        }

        items(
            count = repoList.size,
            key = { index ->
                repoList[index].name ?: "repository_$index"
            },
        ) { index ->
            DetailRepoInfo(
                name = repoList[index].name,
                description = repoList[index].description,
                language = repoList[index].language,
                stargazer = repoList[index].stargazer,

            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailAppBar(
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    favorite: Boolean?,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(text = "") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.icon_content_description_arrow_back),
                )
            }
        },
        actions = {
            IconButton(onClick = onFavoriteClick) {
                if (favorite == true) {
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
        modifier = modifier,
    )
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
