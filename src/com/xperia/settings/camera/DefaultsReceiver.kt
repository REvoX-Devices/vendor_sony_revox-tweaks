/*
 * SPDX-License-Identifier: Apache-2.0
 */
package com.xperia.settings.camera

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings

/**
 * Circle to Search: Settings shows the toggle as on through
 * config_searchAllEntrypointsEnabledDefault, but Launcher3 reads the raw secure setting and treats
 * "unset" as disabled ("Contextual Search invocation failed: setting disabled"). Write the default
 * once so that the toggle and the launcher agree. Once the user changes it, it is left alone.
 */
class DefaultsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val resolver = context.contentResolver
        if (Settings.Secure.getString(resolver, SEARCH_ALL_ENTRYPOINTS) == null) {
            Settings.Secure.putInt(resolver, SEARCH_ALL_ENTRYPOINTS, 1)
        }
        PictogramConfig.apply(context)
    }

    companion object {
        private const val SEARCH_ALL_ENTRYPOINTS = "search_all_entrypoints_enabled"
    }
}
