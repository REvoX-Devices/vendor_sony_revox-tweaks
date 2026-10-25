/*
 * SPDX-License-Identifier: Apache-2.0
 */
package com.xperia.settings.camera

import android.os.Bundle
import android.provider.Settings
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.android.settingslib.widget.SettingsBasePreferenceFragment

class CameraKeySettingsFragment : SettingsBasePreferenceFragment(),
        Preference.OnPreferenceChangeListener {

    private var actionPref: ListPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.camera_key_settings, rootKey)
        actionPref = findPreference<ListPreference>(KEY_ACTION)?.also {
            it.value = getAction().toString()
            it.onPreferenceChangeListener = this
        }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        Settings.System.putInt(requireContext().contentResolver, SETTING, (newValue as String).toInt())
        return true
    }

    private fun getAction(): Int =
        Settings.System.getInt(requireContext().contentResolver, SETTING, DEFAULT_ACTION)

    companion object {
        private const val KEY_ACTION = "camera_key_action"

        // Read by XperiaKeyHandler in system_server (device/sony/sm8250-common/keyhandler)
        const val SETTING = "xperia_camera_key_action"
        const val DEFAULT_ACTION = 1
    }
}
