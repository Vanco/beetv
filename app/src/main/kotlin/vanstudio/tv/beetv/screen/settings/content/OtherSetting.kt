package vanstudio.tv.beetv.screen.settings.content

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
//import com.google.firebase.analytics.FirebaseAnalytics
//import com.google.firebase.crashlytics.ktx.crashlytics
//import com.google.firebase.ktx.Firebase
import vanstudio.tv.beetv.BuildConfig
import vanstudio.tv.beetv.R
import vanstudio.tv.beetv.activities.settings.LogsActivity
import vanstudio.tv.beetv.component.settings.CookiesDialog
import vanstudio.tv.beetv.component.settings.SettingListItem
import vanstudio.tv.beetv.component.settings.SettingSwitchListItem
import vanstudio.tv.beetv.screen.settings.SettingsMenuNavItem
import vanstudio.tv.beetv.util.Prefs

@Composable
fun OtherSetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showCookiesDialog by remember { mutableStateOf(false) }
    var showFps by remember { mutableStateOf(Prefs.showFps) }
    var useOldPlayer by remember { mutableStateOf(Prefs.useOldPlayer) }
    var updateAlpha by remember { mutableStateOf(Prefs.updateAlpha) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = SettingsMenuNavItem.Other.getDisplayName(context),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(12.dp))
        TvLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_other_firebase_title),
                    supportText = stringResource(R.string.settings_other_firebase_text),
                    checked = Prefs.enableFirebaseCollection,
                    onCheckedChange = {
                        Prefs.enableFirebaseCollection = it
//                        Firebase.crashlytics.setCrashlyticsCollectionEnabled(it)
//                        FirebaseAnalytics.getInstance(context)
//                            .setAnalyticsCollectionEnabled(it)
                    }
                )
            }
            item {
                SettingListItem(
                    title = stringResource(R.string.settings_other_cookies_title),
                    supportText = stringResource(R.string.settings_other_cookies_text),
                    onClick = { showCookiesDialog = true }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_other_fps_title),
                    supportText = stringResource(R.string.settings_other_fps_text),
                    checked = showFps,
                    onCheckedChange = {
                        showFps = it
                        Prefs.showFps = it
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_other_old_player_title),
                    supportText = stringResource(R.string.settings_other_old_player_text),
                    checked = useOldPlayer,
                    onCheckedChange = {
                        useOldPlayer = it
                        Prefs.useOldPlayer = it
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_other_alpha_title),
                    supportText = stringResource(R.string.settings_other_alpha_text),
                    checked = updateAlpha,
                    onCheckedChange = {
                        updateAlpha = it
                        Prefs.updateAlpha = it
                    }
                )
            }
            item {
                SettingListItem(
                    title = stringResource(R.string.settings_create_logs_title),
                    supportText = stringResource(R.string.settings_create_logs_text),
                    onClick = {
                        context.startActivity(Intent(context, LogsActivity::class.java))
                    }
                )
            }
            if (BuildConfig.DEBUG) {
                item {
                    SettingListItem(
                        title = stringResource(R.string.settings_crash_test_title),
                        supportText = stringResource(R.string.settings_crash_test_text),
                        onClick = {
                            throw Exception("Boom!")
                        }
                    )
                }
            }
        }
    }

    CookiesDialog(
        show = showCookiesDialog,
        onHideDialog = { showCookiesDialog = false }
    )
}