/*
 * SPDX-License-Identifier: Apache-2.0
 */
package com.xperia.settings.camera

import android.os.Bundle
import com.android.settingslib.widget.SettingsBasePreferenceFragment

class RevoxSettingsFragment : SettingsBasePreferenceFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.revox_settings, rootKey)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().setTitle(R.string.revox_title)
    }
}
