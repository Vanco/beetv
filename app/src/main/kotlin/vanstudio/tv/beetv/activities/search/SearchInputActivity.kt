package vanstudio.tv.beetv.activities.search

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import vanstudio.tv.beetv.screen.search.SearchInputScreen
import vanstudio.tv.beetv.ui.theme.BVTheme

class SearchInputActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                SearchInputScreen()
            }
        }
    }
}