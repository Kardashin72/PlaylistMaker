package com.practicum.playlistmaker.share.domain.api

interface ShareRepository {
    fun getShareLink(): String
    fun getShareChooserText(): String
    fun getSupportEmail(): String
    fun getSupportMessageTopic(): String
    fun getSupportMessage(): String
    fun getUserAgreementLink(): String
}