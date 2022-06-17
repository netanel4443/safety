package com.e.security.ui.activityresults

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class SaveFileResultContract : ActivityResultContract<String, Intent>() {

    override fun createIntent(context: Context, input: String): Intent {

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = input
            putExtra(Intent.EXTRA_TITLE, "שם")
        }
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
        return if (resultCode != Activity.RESULT_OK) null
        else  { intent }
    }
}