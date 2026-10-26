/*
 * SPDX-License-Identifier: Apache-2.0
 */
package net.simplyrin.revoxtweaks

import android.content.Context
import android.os.PersistableBundle
import android.provider.Settings
import android.telephony.CarrierConfigManager
import android.telephony.SubscriptionManager
import android.util.Log

/**
 * Overrides the carrier config values that SystemUI uses to label the mobile network type.
 * Only the switches that are on are overridden, so carrier defaults apply to the rest.
 */
object PictogramConfig {
    private const val TAG = "PictogramConfig"

    const val KEY_4G_FOR_LTE = "xperia_pictogram_4g_for_lte"
    const val KEY_4G_FOR_3G = "xperia_pictogram_4g_for_3g"
    const val KEY_HIDE_LTE_PLUS = "xperia_pictogram_hide_lte_plus"
    const val KEY_5G_STYLE = "xperia_pictogram_5g_style"

    private const val NR_TAIL = ",connected_rrc_idle:5G,not_restricted_rrc_idle:5G," +
        "not_restricted_rrc_con:5G"
    private const val NR_TAIL_PLUS = ",connected_rrc_idle:5G_Plus,not_restricted_rrc_idle:5G_Plus," +
        "not_restricted_rrc_con:5G_Plus"

    fun getInt(context: Context, key: String): Int =
        Settings.Global.getInt(context.contentResolver, key, 0)

    fun putInt(context: Context, key: String, value: Int) {
        Settings.Global.putInt(context.contentResolver, key, value)
    }

    fun apply(context: Context) {
        val overrides = PersistableBundle()
        if (getInt(context, KEY_4G_FOR_LTE) == 1) {
            overrides.putBoolean(CarrierConfigManager.KEY_SHOW_4G_FOR_LTE_DATA_ICON_BOOL, true)
        }
        if (getInt(context, KEY_4G_FOR_3G) == 1) {
            overrides.putBoolean(CarrierConfigManager.KEY_SHOW_4G_FOR_3G_DATA_ICON_BOOL, true)
        }
        if (getInt(context, KEY_HIDE_LTE_PLUS) == 1) {
            overrides.putBoolean(CarrierConfigManager.KEY_HIDE_LTE_PLUS_DATA_ICON_BOOL, true)
        }
        when (getInt(context, KEY_5G_STYLE)) {
            1 -> overrides.putString(CarrierConfigManager.KEY_5G_ICON_CONFIGURATION_STRING,
                "connected_mmwave:5G_Plus,connected:5G$NR_TAIL")
            2 -> overrides.putString(CarrierConfigManager.KEY_5G_ICON_CONFIGURATION_STRING,
                "connected_mmwave:5G_Plus,connected:5G_Plus$NR_TAIL_PLUS")
        }

        val subs = context.getSystemService(SubscriptionManager::class.java)
            ?.activeSubscriptionInfoList ?: return
        val manager = context.getSystemService(CarrierConfigManager::class.java) ?: return
        val prefs = context.getSharedPreferences("pictogram_applied", Context.MODE_PRIVATE)
        val fingerprint = overrides.toString()
        for (info in subs) {
            // overrideConfig() broadcasts CARRIER_CONFIG_CHANGED, which updates the subscriptions
            // and calls us again; only write when the overrides differ from the last ones applied.
            val key = info.subscriptionId.toString()
            if (prefs.getString(key, null) == fingerprint) continue
            try {
                // null clears our previous overrides; this app is the only user of them
                manager.overrideConfig(info.subscriptionId,
                    if (overrides.isEmpty) null else overrides, true)
                prefs.edit().putString(key, fingerprint).apply()
            } catch (e: Exception) {
                Log.e(TAG, "overrideConfig failed for ${info.subscriptionId}", e)
            }
        }
    }

    // ---- per-network-type icon choice, read by SystemUI (PictogramOverride) ----
    const val KEY_ICONS = "revox_pictogram_icons"
    const val SYSTEMUI = "com.android.systemui"
    val ICON_SLOTS = listOf("3g", "3g+", "4g", "4g+", "lte", "lte+", "5g", "5g+")

    fun getIconChoices(context: Context): Map<String, String> =
        (Settings.Global.getString(context.contentResolver, KEY_ICONS) ?: "")
            .split(';').mapNotNull {
                val kv = it.split('=', limit = 2)
                if (kv.size == 2 && kv[1].isNotEmpty()) kv[0] to kv[1] else null
            }.toMap()

    /** [name] null restores the default icon for [slot]. */
    fun setIconChoice(context: Context, slot: String, name: String?) {
        val map = getIconChoices(context).toMutableMap()
        if (name == null) map.remove(slot) else map[slot] = name
        Settings.Global.putString(context.contentResolver, KEY_ICONS,
            map.entries.joinToString(";") { "${it.key}=${it.value}" })
    }

    /** SystemUI's resources, so that the picker shows exactly what the status bar will draw. */
    fun systemUiResources(context: Context) =
        context.packageManager.getResourcesForApplication(SYSTEMUI)

    /** Drawable names offered in the picker: SystemUI's list plus the device's extra entries. */
    fun iconCatalog(context: Context): List<String> {
        val res = systemUiResources(context)
        return listOf("config_pictogramIcons", "config_pictogramIconsExtra").flatMap {
            val id = res.getIdentifier(it, "array", SYSTEMUI)
            if (id != 0) res.getStringArray(id).toList() else emptyList()
        }
    }
}
