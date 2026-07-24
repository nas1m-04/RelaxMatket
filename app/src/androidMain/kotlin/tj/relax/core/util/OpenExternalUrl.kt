package tj.relax.core.util

import android.content.Intent
import android.net.Uri

actual fun openExternalUrl(url: String) {
    AndroidPlatformContext.applicationContext.startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}
