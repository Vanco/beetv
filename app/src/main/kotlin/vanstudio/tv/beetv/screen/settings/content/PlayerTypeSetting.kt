package vanstudio.tv.beetv.screen.settings.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import vanstudio.tv.beetv.component.LibVLCDownloaderDialog
import vanstudio.tv.beetv.component.settings.SettingsMenuSelectItem
import vanstudio.tv.beetv.entity.PlayerType
import vanstudio.tv.beetv.screen.settings.SettingsMenuNavItem
import vanstudio.tv.beetv.util.Prefs

@Composable
fun PlayerTypeSetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPlayerType by remember { mutableStateOf(Prefs.playerType) }
    var showLibVLCDownloaderDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = SettingsMenuNavItem.PlayerType.getDisplayName(context),
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            TvLazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = PlayerType.entries) { playerType ->
                    SettingsMenuSelectItem(
                        text = playerType.name,
                        selected = selectedPlayerType == playerType,
                        onClick = {
                            selectedPlayerType = playerType
                            Prefs.playerType = playerType
                        }
                    )
                }
            }
        }
    }

    LibVLCDownloaderDialog(
        show = showLibVLCDownloaderDialog,
        onHideDialog = {
            showLibVLCDownloaderDialog = false
        }
    )
}
