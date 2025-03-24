package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.net.toUri
import com.google.android.material.button.MaterialButton

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<Button>(R.id.settings_back)

        backButton.setOnClickListener {
            finish()
        }

        val shareButton = findViewById<Button>(R.id.shareButton)
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
                type = "text/plain"
            }
            val chooser = Intent.createChooser(shareIntent, getString(R.string.share_chooser_text))
            startActivity(chooser)
        }

        //изначально пытался сделать через
        //     val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
        //         data = Uri.parse("mailto:${getString(R.string.support_email)}")
        //         putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_message_topic))
        //         putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
        //     }
        //но этот вариант на моем смартфоне стабильно выдавал Toast-заглушку, хотя есть Gmail по умолчанию
        //текущий вариант работает через выбор приложения
        val supportButton = findViewById<Button>(R.id.supportButton)
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_message_topic))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            }
            if (supportIntent.resolveActivity(packageManager) != null) {
                startActivity(supportIntent)
            } else {
                Toast.makeText(this, R.string.mail_app_not_found, Toast.LENGTH_SHORT).show()
            }
        }

        val userAgreementButton = findViewById<Button>(R.id.userAgreementButton)
        userAgreementButton.setOnClickListener {
            val userAgreementIntent = Intent(Intent.ACTION_VIEW).apply {

                data = getString(R.string.user_agreement_link).toUri()
            }
            startActivity(userAgreementIntent)
        }
    }
}