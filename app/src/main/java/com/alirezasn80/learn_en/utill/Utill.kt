package com.alirezasn80.learn_en.utill

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.Settings
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import com.alirezasn80.learn_en.R
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.TimeoutCancellationException
import org.json.JSONArray
import java.io.IOException
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.random.Random

const val DEBUG = true
fun debug(message: String?, tag: String = "AppDebug") {
    if (DEBUG)
        Log.d(tag, "********DEBUG********\n$message")
}

@Composable
fun Any.toStr(): String {
    return when (this) {
        is Int -> stringResource(id = this)
        is String -> this
        else -> ""
    }
}

object User {
    var isVipUser = false
}


object ScreenType {
    const val ALL = "ALL"
    const val IMAGE = "IMAGE"
    const val VIDEO = "VIDEO"
    const val DOCUMENT = "DOCUMENT"
    const val APK = "APK"
    const val MUSIC = "MUSIC"
    const val OTHER = "OTHER"
    const val CLEANUP = "CLEANUP"
}

object Key {
    const val ONBOARDING = "OnBoarding"
    const val COMMENT = "Comment"
    const val IS_VIP = "IS_VIP"
    const val COUNTER = "Counter"
    const val NEGATIVE = "NEGATIVE"
    const val POSITIVE = "POSITIVE"
    const val IS_DARK_THEME = "IS_DARK_THEME"
    const val SAF_PERMISSION = "SAF_PERMISSION"
    const val STORAGE_PERMISSION = "StoragePermission"
    const val EXPIRE_DATE = "EXPIRE_DATE"
}

sealed interface WidgetType {
    object Toast : WidgetType
    object Snackbar : WidgetType
}

sealed interface Destination {
    object Home : Destination
    object OnBoarding : Destination
    object Back : Destination
    object Offline : Destination
    object Payment : Destination
}

sealed interface MessageState {
    object Error : MessageState
    object Success : MessageState
    //  object Info : MessageState
}

data class RemoteError(@StringRes val message: Int, val code: Int? = null)

object LoadingKey {
    const val DEFAULT = ""
    const val NEXT_PAGE = "NextPage"
    const val DICT = "DICT"
    const val IMG = "IMG"
}


fun Throwable.toRemoteError() = when (this) {

    is IOException -> RemoteError(com.alirezasn80.learn_en.R.string.network_error)

    // is HttpException -> RemoteError(R.string.http_error, this.code())

    is TimeoutCancellationException -> RemoteError(R.string.timeout)

    is NullPointerException -> RemoteError(R.string.network_data_null)

    else -> RemoteError(R.string.unknown_error)

}

sealed class Progress {

    object Loading : Progress()

    object Idle : Progress()

}

data class UiComponent(
    val message: Any?,
    val widgetType: WidgetType,
    val messageState: MessageState,
)

interface MessageType {
    fun setMessageByToast(message: Any, messageState: MessageState = MessageState.Error)
    fun setMessageBySnackbar(message: Any, messageState: MessageState = MessageState.Error)

}


@Suppress("RegExpRedundantEscape")
fun String.arguments(): Sequence<MatchResult> {
    val argumentRegex = "\\{(.*?)\\}".toRegex()
    return argumentRegex.findAll(this)
}

inline val String.argumentCount: Int get() = arguments().count()

object Arg {
    const val IS_TRIAL: String = "isTrial"
    const val Key = "Key"
    const val CATEGORY_ID = "CATEGORY_ID"
    const val CONTENT_ID = "CONTENT_ID"
    const val TITLE = "TITLE"
}

suspend fun withDuration(title: String = "", content: suspend () -> Unit) {
    val startTime = System.currentTimeMillis()
    content()
    val timeMillis = System.currentTimeMillis() - startTime
    val timeSecond = TimeUnit.MILLISECONDS.toSeconds(timeMillis)
    val timeMinute = TimeUnit.MILLISECONDS.toMinutes(timeMillis)
    val result =
        "${if (timeMinute != 0L) "${timeMinute}m = " else ""}${if (timeSecond != 0L) "${timeSecond}s = " else ""}${timeMillis}ms".trim()
    AppMetrica.reportEvent("Execution Time", mapOf(title to result))
    debug("\n==============================$title==============================\nExecution Time -> $result\n=======================================================================================================================")
}

@RequiresApi(Build.VERSION_CODES.Q)
fun createSAFintent(context: Context, dir: String): Intent {

    val rootFolder = "primary:"
    val documentId = "${rootFolder}${dir}"
    val authority = "com.android.externalstorage.documents"
    val uri = DocumentsContract.buildDocumentUri(authority, documentId)

    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
    //  (context.getSystemService(Context.STORAGE_SERVICE) as StorageManager).primaryStorageVolume.createOpenDocumentTreeIntent()
    // intent.addCategory(Intent.CATEGORY_OPENABLE)
    //intent.setDataAndType(uri, DocumentsContract.Document.MIME_TYPE_DIR)
    //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    // intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
    return intent


    //val storageManager = (context.getSystemService(Context.STORAGE_SERVICE) as StorageManager)
    //return storageManager.primaryStorageVolume.createOpenDocumentTreeIntent().putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)


    /*debug("dir : $dir")
    val intent = (context.getSystemService(Context.STORAGE_SERVICE) as StorageManager).primaryStorageVolume.createOpenDocumentTreeIntent()
    var startDir = dir
    var uri = intent.getParcelableExtra<Uri>(DocumentsContract.EXTRA_INITIAL_URI)
    var scheme = uri.toString()
    scheme = scheme.replace("/root/", "/document/")
    startDir = startDir.replace("/", "%2F")
    scheme += "%3A$startDir"
    uri = Uri.parse(scheme)
    debug("uri : $uri")
    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
    return intent*/
}

fun Context.isStoragePermissionGranted() = listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).all {
    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

fun Context.isInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

}

fun String.separateNumber(): String {
    if (this.isEmpty()) return "0"
    if (this.contains("0.00")) return "0"
    return try {
        "%,d".format(this.toInt())
    } catch (t: Throwable) {
        this
    }
}

fun Context.shareText(textId: Int) {
    try {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(textId))
            type = "text/plain"
        }
        ContextCompat.startActivity(this, Intent.createChooser(sendIntent, ""), null)
    } catch (e: Exception) {
        AppMetrica.reportError("Problem to share text with intent", e)
    }

}

fun Context.showToast(textId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, textId, duration).show()
}

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Context.openGmail(isFeedBack: Boolean = true) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("sn80.learnen@gmail.com"))
        if (isFeedBack)
            intent.putExtra(Intent.EXTRA_SUBJECT, "بازخورد برنامه")
        this.startActivity(Intent.createChooser(intent, ""))
    } catch (e: Exception) {
        AppMetrica.reportError("can't open Gmail", e)
        Toast.makeText(this, "لطفا نظر خود را به ادرس sn80.learnen@gmail.com ارسال کنید", Toast.LENGTH_SHORT).show()
    }

}

fun Context.openBrowser(url: String?) {
    url?.let {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(it)
        startActivity(intent)
    }
}

fun Context.openBazaarComment() {
    try {
        val intent = Intent(Intent.ACTION_EDIT)
        intent.setData(Uri.parse("bazaar://details?id=com.alirezasn80.learn_en"))
        intent.setPackage("com.farsitel.bazaar")
        startActivity(intent)
    } catch (e: Exception) {
        AppMetrica.reportError("Error : Open Cafe Bazaar to send comment", e)
    }

}

fun Context.openAppInCafeBazaar() {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse("bazaar://details?id=com.alirezasn80.eitaacleaner"))
        intent.setPackage("com.farsitel.bazaar")
        startActivity(intent)
    } catch (e: Exception) {
        AppMetrica.reportError("Error : Open App In Cafe Bazaar ", e)
    }

}

fun Context.getAndroidId() = try {
    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
} catch (e: Exception) {
    e.printStackTrace()
    "null"
}

fun Context.getVersionName() = try {
    packageManager.getPackageInfo(packageName, 0).versionName
} catch (e: PackageManager.NameNotFoundException) {
    AppMetrica.reportError("Problem to get Version name", e)
    "ایتا کلینر"
}

fun Long.text(): String {
    val decFormat = DecimalFormat("#,##0.00")

    return try {
        when {
            this == 0.toLong() -> "خالی"
            this < 1024 -> "${this.toInt()} Bytes"
            this < 1024 * 1024 -> "${decFormat.format(this / 1024.0)} KB" // استفاده از تابع format برای نمایش دو رقم اعشار
            this < 1024 * 1024 * 1000 -> "${decFormat.format(this / (1024 * 1024.0))} MB"
            else -> "${decFormat.format(this / (1024 * 1024 * 1024.0))} GB"
        }
    } catch (e: Exception) {
        AppMetrica.reportError("Problem to Calculate the size as bytes/kb/mb/gb", e)
        "${this.toInt()} Bytes"
    }
}

fun NavBackStackEntry.getStringArg(key: String): String {
    return arguments?.getString(key)!!
}

fun NavBackStackEntry.getIntArg(key: String): Int? {
    return try {
        arguments?.getString(key)?.toInt()
    } catch (e: Exception) {
        null
    }
}

fun SavedStateHandle.getInt(key: String): Int? {
    return try {
        this.get<String>(key)?.toInt()
    } catch (e: Exception) {
        null
    }
}

fun SavedStateHandle.getString(key: String) = this.get<String>(key)

@Composable
fun randomColor(): Color {
    val alpha = Random.nextInt(200, 256)
    val red = Random.nextInt(256)
    val green = Random.nextInt(256)
    val blue = Random.nextInt(256)
    return MaterialTheme.colorScheme.primary.copy(alpha = Random.nextDouble(0.2f.toDouble(), 1f.toDouble()).toFloat())
}

@Composable
fun Rtl(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        content()
    }
}

@Composable
fun Ltr(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        content()
    }
}

fun String.removeBlankLines() = this.lines().filter { it.isNotBlank() }.joinToString("\n")

fun JSONArray.toStringList(): List<String> {
    val list = mutableListOf<String>()
    for (i in 0 until this.length()) {
        list.add(this.getString(i))
    }
    return list
}

enum class Keyboard {
    Opened, Closed
}

@Composable
fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Opened
            } else {
                Keyboard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}

fun createImageBitmap(context: Context, path: String): ImageBitmap {
    return BitmapFactory.decodeStream(context.assets.open(path)).asImageBitmap()
}

fun Int.toBoolean() = if (this == 1) true else false
fun Boolean.toLogicInt() = if (this == true) 1 else 0

fun String.cleanWord(): String {
    return this.trim { it !in 'a'..'z' && it !in 'A'..'Z' && it !in '0'..'9' }
}

sealed class DictCategory(val id: Int, val title: String) {
    data object Meaning : DictCategory(1, "معانی")
    data object Desc : DictCategory(2, "توضیحات")
    data object Example : DictCategory(3, "مثال")
}
