package com.practicum.playlistmaker.share.data

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.share.domain.api.ShareRepository

class ShareRepositoryImpl(private val context: Context) : ShareRepository {
    override fun getShareLink(): String {
        return context.getString(R.string.share_link)
    }

    override fun getShareChooserText(): String {
        return context.getString(R.string.share_chooser_text)
    }

    override fun getSupportEmail(): String {
        return context.getString(R.string.support_email)
    }

    override fun getSupportMessageTopic(): String {
        return context.getString(R.string.support_message_topic)
    }

    override fun getSupportMessage(): String {
        return context.getString(R.string.support_message)
    }

    override fun getUserAgreementLink(): String {
        return context.getString(R.string.user_agreement_link)
    }
}