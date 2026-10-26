/*
 * SPDX-License-Identifier: Apache-2.0
 */
package net.simplyrin.revoxtweaks

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
        for (slot in PictogramConfig.ICON_SLOTS) {
            findPreference<Preference>(ICON_PREFIX + slot)?.let {
                it.title = slot.uppercase()
                it.setOnPreferenceClickListener { showIconPicker(slot, it) }
                refreshIcon(slot, it)
            }
        }
        findPreference<ListPreference>(KEY_5G)?.let {
            it.value = PictogramConfig.getInt(ctx, PictogramConfig.KEY_5G_STYLE).toString()
            it.onPreferenceChangeListener = this
        }
    }

    private fun drawableOf(name: String): Drawable? = try {
        val res = PictogramConfig.systemUiResources(requireContext())
        val id = res.getIdentifier(name, "drawable", PictogramConfig.SYSTEMUI)
        if (id != 0) res.getDrawable(id, null) else null
    } catch (e: Exception) { null }

    private fun refreshIcon(slot: String, pref: Preference) {
        val name = PictogramConfig.getIconChoices(requireContext())[slot]
        pref.summary = name?.let { label(it) } ?: getString(R.string.pictogram_icon_default)
        pref.icon = name?.let { drawableOf(it) }
    }

    private fun label(name: String) = name.removePrefix("ic_").removeSuffix("_mobiledata")

    private fun showIconPicker(slot: String, pref: Preference): Boolean {
        val ctx = requireContext()
        val names = listOf<String?>(null) + PictogramConfig.iconCatalog(ctx)
        val adapter = object : ArrayAdapter<String?>(ctx, 0, names) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val name = getItem(position)
                val row = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = android.view.Gravity.CENTER_VERTICAL
                    val pad = (16 * resources.displayMetrics.density).toInt()
                    setPadding(pad, pad / 2, pad, pad / 2)
                }
                val size = (32 * ctx.resources.displayMetrics.density).toInt()
                val image = ImageView(ctx).apply {
                    name?.let { setImageDrawable(drawableOf(it)) }
                    // Status bar icons are drawn in a single colour; show them in the text colour.
                    val attrs = ctx.obtainStyledAttributes(intArrayOf(android.R.attr.textColorPrimary))
                    imageTintList = attrs.getColorStateList(0)
                    attrs.recycle()
                }
                row.addView(image, LinearLayout.LayoutParams(size, size))
                row.addView(TextView(ctx).apply {
                    text = name?.let { label(it) } ?: getString(R.string.pictogram_icon_default)
                    textSize = 16f
                    setPadding(size / 2, 0, 0, 0)
                })
                return row
            }
        }
        AlertDialog.Builder(ctx)
            .setTitle(getString(R.string.pictogram_icon_choose) + ": " + slot.uppercase())
            .setAdapter(adapter) { _, which ->
                PictogramConfig.setIconChoice(ctx, slot, names[which])
                refreshIcon(slot, pref)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
        return true
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
        private const val ICON_PREFIX = "pictogram_icon_"
        private const val KEY_5G = "pictogram_5g_style"
        private val SWITCHES = mapOf(
            "pictogram_4g_for_lte" to PictogramConfig.KEY_4G_FOR_LTE,
            "pictogram_4g_for_3g" to PictogramConfig.KEY_4G_FOR_3G,
            "pictogram_hide_lte_plus" to PictogramConfig.KEY_HIDE_LTE_PLUS,
        )
    }
}
