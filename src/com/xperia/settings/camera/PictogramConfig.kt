/*
 * SPDX-License-Identifier: Apache-2.0
 */
package com.xperia.settings.camera

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
        for (info in subs) {
            try {
                // null clears our previous overrides; this app is the only user of them
                manager.overrideConfig(info.subscriptionId,
                    if (overrides.isEmpty) null else overrides, true)
            } catch (e: Exception) {
                Log.e(TAG, "overrideConfig failed for ${info.subscriptionId}", e)
            }
        }
    }
}
