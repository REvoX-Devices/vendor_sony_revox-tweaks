# vendor_sony_revox-tweaks

「REvoX 設定」アプリ (`RevoxTweaks`)。設定の「システム」の詳細に表示されます。

- カメラ: カメラボタンの動作 (オフ / カメラ / Photo Pro)
- ピクト表示: LTE を 4G、3G を 4G、LTE+ を LTE と表示、5G / 5G+ の表示

ピクト表示は `CarrierConfigManager.overrideConfig` でキャリア設定を上書きします (frameworks/base は変更しません)。

`device/sony/sm8250-common/edo.mk` が `vendor/sony/revox-tweaks/revox-tweaks.mk` を継承します。
