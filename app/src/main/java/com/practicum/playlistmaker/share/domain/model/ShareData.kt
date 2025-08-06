package com.practicum.playlistmaker.share.domain.model

data class ShareAppData(
    val shareLink: String,
    val chooserText: String
)

data class ContactSupportData(
    val supportEmail: String,
    val messageTopic: String,
    val message: String
)

data class UserAgreementData(
    val userAgreementLink: String
)