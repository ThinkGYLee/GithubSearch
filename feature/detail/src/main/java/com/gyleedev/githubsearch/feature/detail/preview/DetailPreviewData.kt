package com.gyleedev.githubsearch.feature.detail.preview

import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.UserModel

object DetailPreviewData {
    val skydovesUser =
        UserModel(
            name = "Jaewoong Eum",
            followers = 12783,
            following = 19,
            avatar = "https://avatars.githubusercontent.com/u/24237865?v=4",
            company = "@RevenueCat",
            email = "dove@dove.net",
            bio =
                "Senior Developer Relations & Engineer @RevenueCat 🥑 • GDE for Android & Kotlin & Firebase • Open Source Software ❤️  • Coffee Lover • Found @doveletter",
            repoCount = 82,
            reposAddress = "https://api.github.com/users/skydoves/repos",
            blogUrl = "https://doveletter.dev",
            favorite = false,
            id = 0L,
            login = "skydoves",
            createdDate = "",
            updatedDate = "",
        )

    val skydovesRepos =
        listOf(
            RepositoryModel(
                userGithubId = "skydoves",
                name = "All-In-One",
                description =
                    ":necktie: Health care application for reminding health-todo lists and making healthy habits every day.",
                language = "Kotlin",
                stargazer = 122,
            ),
            RepositoryModel(
                userGithubId = "skydoves",
                name = "android-developer-roadmap",
                description =
                    " 🗺 The Android Developer Roadmap offers comprehensive learning paths to help you understand Android ecosystems.",
                language = "Kotlin",
                stargazer = 7748,
            ),
            RepositoryModel(
                userGithubId = "skydoves",
                name = "android-skills-mcp",
                description = "An MCP server and CLI packager for official Android skills.",
                language = "TypeScript",
                stargazer = 193,
            ),
            RepositoryModel(
                userGithubId = "skydoves",
                name = "android-testing-skills",
                description =
                    "⚡️ A set of skills for Android testing: Compose UI, AndroidX Test, JVM unit tests, and ADB.",
                language = "Shell",
                stargazer = 185,
            ),
            RepositoryModel(
                userGithubId = "skydoves",
                name = "AndroidBottomBar",
                description =
                    " 🍫 A lightweight bottom navigation view, fully customizable with an indicator and animations.",
                language = "Kotlin",
                stargazer = 293,
            ),
        )
}
