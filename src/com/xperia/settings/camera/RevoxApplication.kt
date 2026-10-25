/*
 * SPDX-License-Identifier: Apache-2.0
 */
package com.xperia.settings.camera

import android.app.Application
import android.telephony.SubscriptionManager

/** Re-applies the pictogram overrides whenever the active subscriptions change (SIM swap, boot). */
class RevoxApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        getSystemService(SubscriptionManager::class.java)?.addOnSubscriptionsChangedListener(
            mainExecutor,
            object : SubscriptionManager.OnSubscriptionsChangedListener() {
                override fun onSubscriptionsChanged() {
                    PictogramConfig.apply(this@RevoxApplication)
                }
            })
    }
}
