package com.example

import android.app.Activity
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import com.lagradost.cloudstream3.R
import com.lagradost.cloudstream3.AcraApplication.Companion.getKey
import com.lagradost.cloudstream3.AcraApplication.Companion.setKey
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.XmlResourceParser
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.lagradost.cloudstream3.utils.UIHelper.colorFromAttribute
import kotlin.system.exitProcess

const val APP_PSK_KEY: String = "app_psk"

@CloudstreamPlugin
class PasswordPlugin: Plugin() {

    private fun getLayout(name: String): XmlResourceParser? {
        val id = this.resources?.getIdentifier(name, "layout", BuildConfig.LIBRARY_PACKAGE_NAME) ?: return null
        return this.resources?.getLayout(id)
    }

    override fun load(context: Context) {
        if (this.resources == null) return;
        (context as Activity).runOnUiThread {
            val li = LayoutInflater.from(context)
            val layout = getLayout("login_dialog") ?: return@runOnUiThread
            val view = li.inflate(layout, null)

            val alert = AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .create()

            val input = view.findViewById<EditText>(this.resources!!.getIdentifier("password", "id", BuildConfig.LIBRARY_PACKAGE_NAME))
            val btn = view.findViewById<Button>(this.resources!!.getIdentifier("loginButton", "id", BuildConfig.LIBRARY_PACKAGE_NAME))
            btn.text = context.getString(R.string.login)
            btn.setTextColor(ColorStateList.valueOf(view.context.colorFromAttribute(R.attr.white)))
            btn.setOnClickListener {
                val psk = getKey<String>(APP_PSK_KEY)
                if (psk == null) {
                    setKey(APP_PSK_KEY, input.text.toString())
                } else {
                    if (psk != input.text.toString()) {
                        exitProcess(1)
                    }
                    alert.dismiss()
                }
            }
            alert.show()
        }
    }
}