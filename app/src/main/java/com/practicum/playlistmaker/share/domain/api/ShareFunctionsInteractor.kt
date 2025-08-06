package com.practicum.playlistmaker.share.domain.api

import com.practicum.playlistmaker.share.domain.model.ContactSupportData
import com.practicum.playlistmaker.share.domain.model.ShareAppData
import com.practicum.playlistmaker.share.domain.model.UserAgreementData

interface ShareFunctionsInteractor {
    fun getShareData(): ShareAppData
    fun getUserAgreementData(): UserAgreementData
    fun getSupportData(): ContactSupportData
}