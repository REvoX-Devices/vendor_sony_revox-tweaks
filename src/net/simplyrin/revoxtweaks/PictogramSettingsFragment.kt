/*
 * SPDX-License-Identifier: Apache-2.0
 */
package net.simplyrin.revoxtweaks

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.android.settingslib.widget.SettingsBasePreferenceFragment

class PictogramSettingsFragment : SettingsBasePreferenceFragment(),
        Preference.OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pictogram_settings, rootKey)
        val ctx = requireContext()
        for ((prefKey, settingKey) in SWITCHES) {
            findPreference<SwitchPreferenceCompat>(prefKey)?.let {
                it.isChecked = PictogramConfig.getInt(ctx, settingKey) == 1
                it.onPreferenceChangeListener = this
            }
        }
        findPreference<ListPreference>(KEY_5G)?.let {
            it.value = PictogramConfig.getInt(ctx, PictogramConfig.KEY_5G_STYLE).toString()
            it.onPreferenceChangeListener = this
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().setTitle(R.string.revox_pictogram_title)
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        val ctx = requireContext()
        val settingKey = SWITCHES[preference.key]
        if (settingKey != null) {
            PictogramConfig.putInt(ctx, settingKey, if (newValue as Boolean) 1 else 0)
        } else if (preference.key == KEY_5G) {
            PictogramConfig.putInt(ctx, PictogramConfig.KEY_5G_STYLE, (newValue as String).toInt())
        } else {
            return false
        }
        PictogramConfig.apply(ctx)
        return true
    }

    companion object {
        private const val KEY_5G = "pictogram_5g_style"
        private val SWITCHES = mapOf(
            "pictogram_4g_for_lte" to PictogramConfig.KEY_4G_FOR_LTE,
            "pictogram_4g_for_3g" to PictogramConfig.KEY_4G_FOR_3G,
            "pictogram_hide_lte_plus" to PictogramConfig.KEY_HIDE_LTE_PLUS,
        )
    }
}
