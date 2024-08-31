package vanstudio.tv.beetv.component.controllers2.playermenu

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import vanstudio.tv.beetv.component.controllers2.VideoPlayerMenuNavItem
import vanstudio.tv.beetv.component.controllers2.playermenu.component.MenuListItem
import vanstudio.tv.beetv.component.createCustomInitialFocusRestorerModifiers
import vanstudio.tv.beetv.component.ifElse

@Composable
fun MenuNavList(
    modifier: Modifier = Modifier,
    selectedMenu: VideoPlayerMenuNavItem,
    onSelectedChanged: (VideoPlayerMenuNavItem) -> Unit,
    isFocusing: Boolean
) {
    val context = LocalContext.current
    val focusRestorerModifiers = createCustomInitialFocusRestorerModifiers()

    LazyColumn(
        modifier = modifier
            .animateContentSize()
            .then(focusRestorerModifiers.parentModifier),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(VideoPlayerMenuNavItem.entries) { index, item ->
            MenuListItem(
                modifier = Modifier
                    .ifElse(index == 0, focusRestorerModifiers.childModifier),
                text = item.getDisplayName(context),
                icon = item.icon,
                expanded = isFocusing,
                selected = selectedMenu == item,
                onClick = {},
                onFocus = { onSelectedChanged(item) },
            )
        }
    }
}