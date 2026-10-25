/*
 * SPDX-License-Identifier: Apache-2.0
 */
package com.xperia.settings.camera

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity

class RevoxSettingsActivity : CollapsingToolbarBaseActivity(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(com.android.settingslib.collapsingtoolbar.R.id.content_frame,
                    RevoxSettingsFragment(), TAG)
                .commit()
        }
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat, pref: Preference
    ): Boolean {
        val fragment: Fragment = supportFragmentManager.fragmentFactory
            .instantiate(classLoader, pref.fragment ?: return false)
        fragment.arguments = pref.extras
        supportFragmentManager.beginTransaction()
            .replace(com.android.settingslib.collapsingtoolbar.R.id.content_frame, fragment)
            .addToBackStack(null)
            .commit()
        title = pref.title
        return true
    }

    override fun onNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            setTitle(R.string.revox_title)
            return true
        }
        return super.onNavigateUp()
    }

    companion object {
        private const val TAG = "RevoxSettingsActivity"
    }
}
