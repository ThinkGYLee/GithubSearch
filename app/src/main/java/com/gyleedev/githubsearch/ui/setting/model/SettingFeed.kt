package com.gyleedev.githubsearch.ui.setting.model

sealed interface SettingFeed {

    data class Title(
        val content: Int
    ) : SettingFeed

    data class Dialog(
        val content: Int,
        val type: SettingDialogEnum
    ) : SettingFeed

    data class Version(
        val content: Int
    ): SettingFeed

}