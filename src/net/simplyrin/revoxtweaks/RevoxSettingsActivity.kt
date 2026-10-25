/*
 * SPDX-License-Identifier: Apache-2.0
 */
package net.simplyrin.revoxtweaks

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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
        // Animator based transitions with reordering allowed are what lets the predictive back
        // gesture preview the pop of this back stack entry
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
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
