package com.practicum.playlistmaker.share.domain.impl

import com.practicum.playlistmaker.share.domain.api.ShareFunctionsInteractor
import com.practicum.playlistmaker.share.domain.api.ShareRepository
import com.practicum.playlistmaker.share.domain.model.ContactSupportData
import com.practicum.playlistmaker.share.domain.model.ShareAppData
import com.practicum.playlistmaker.share.domain.model.UserAgreementData

class ShareFunctionsInteractorImpl(private val repository: ShareRepository) : ShareFunctionsInteractor {
    override fun getShareData(): ShareAppData {
        return ShareAppData(
            shareLink = repository.getShareLink(),
            chooserText = repository.getShareChooserText())
    }

    override fun getUserAgreementData(): UserAgreementData {
        return UserAgreementData(userAgreementLink = repository.getUserAgreementLink())
    }

    override fun getSupportData(): ContactSupportData {
        return ContactSupportData(
            supportEmail = repository.getSupportEmail(),
            messageTopic = repository.getSupportMessageTopic(),
            message = repository.getSupportMessage()
        )
    }
}