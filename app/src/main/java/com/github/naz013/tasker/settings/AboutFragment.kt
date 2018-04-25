package com.github.naz013.tasker.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.NestedFragment
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.fragment_info.*


/**
 * Copyright 2018 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class AboutFragment : NestedFragment() {

    companion object {
        const val TAG = "AboutFragment"
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.onClick { navInterface?.moveBack() }
        rateButton.onClick { openAppScreen() }
        moreButton.onClick { openAuthorScreen() }
        gitButton.onClick { openSourceScreen() }
        feedbackButton.onClick { openFeedbackScreen() }

        showVersion()
    }

    private fun showVersion() {
        val pInfo: PackageInfo
        try {
            pInfo = context!!.packageManager.getPackageInfo(context!!.packageName, 0)
            versionView.text = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

    private fun openFeedbackScreen() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "plain/text"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("feedback.cray@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        activity!!.startActivity(Intent.createChooser(emailIntent, "Send mail..."))
    }

    private fun openSourceScreen() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/naz013/my-day-todo"))
        startActivity(intent)
    }

    private fun openAuthorScreen() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://search?q=pub:Nazar Suhovich")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
        }

    }

    private fun openAppScreen() {
        val uri = Uri.parse("market://details?id=" + context?.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
        }
    }
}