package com.alirezasn80.learn_en.utill

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.*
import kotlin.concurrent.schedule

@Composable
fun rememberMessageBarState(): MessageBarState {
    return remember { MessageBarState() }
}

enum class MessageBarPosition {
    TOP,
    BOTTOM
}

@Composable
fun ContentWithMessageBar(
    modifier: Modifier = Modifier,
    messageBarState: MessageBarState,
    position: MessageBarPosition = MessageBarPosition.TOP,
    visibilityDuration: Long = 3000L,
    showToastOnCopy: Boolean = false,
    successIcon: ImageVector = Icons.Default.Check,
    errorIcon: ImageVector = Icons.Default.Warning,
    errorMaxLines: Int = 1,
    successMaxLines: Int = 1,
    contentBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    successContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    successContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    errorContainerColor: Color = MaterialTheme.colorScheme.errorContainer,
    errorContentColor: Color = MaterialTheme.colorScheme.onErrorContainer,
    enterAnimation: EnterTransition = expandVertically(
        animationSpec = tween(durationMillis = 300),
        expandFrom = if (position == MessageBarPosition.TOP)
            Alignment.Top else Alignment.Bottom
    ),
    exitAnimation: ExitTransition = shrinkVertically(
        animationSpec = tween(durationMillis = 300),
        shrinkTowards = if (position == MessageBarPosition.TOP)
            Alignment.Top else Alignment.Bottom
    ),
    verticalPadding: Dp = 12.dp,
    horizontalPadding: Dp = 12.dp,
    content: @Composable () -> Unit,
) {

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = contentBackgroundColor)
        ) {
            content()

                MessageBarComponent(
                    messageBarState = messageBarState,
                    position = position,
                    visibilityDuration = visibilityDuration,
                    successIcon = successIcon,
                    errorIcon = errorIcon,
                    errorMaxLines = errorMaxLines,
                    successMaxLines = successMaxLines,
                    successContainerColor = successContainerColor,
                    successContentColor = successContentColor,
                    errorContainerColor = errorContainerColor,
                    errorContentColor = errorContentColor,
                    enterAnimation = enterAnimation,
                    exitAnimation = exitAnimation,
                    verticalPadding = verticalPadding,
                    horizontalPadding = horizontalPadding,
                    showToastOnCopy = showToastOnCopy
                )


        }


}

@Composable
internal fun MessageBarComponent(
    messageBarState: MessageBarState,
    position: MessageBarPosition,
    visibilityDuration: Long,
    successIcon: ImageVector,
    errorIcon: ImageVector,
    errorMaxLines: Int,
    successMaxLines: Int,
    successContainerColor: Color,
    successContentColor: Color,
    errorContainerColor: Color,
    errorContentColor: Color,
    enterAnimation: EnterTransition,
    exitAnimation: ExitTransition,
    verticalPadding: Dp,
    horizontalPadding: Dp,
    showToastOnCopy: Boolean,
) {
    var showMessageBar by remember { mutableStateOf(false) }
    val error by rememberUpdatedState(newValue = messageBarState.error?.message)
    val message by rememberUpdatedState(newValue = messageBarState.success)

    DisposableEffect(key1 = messageBarState.updated) {
        showMessageBar = true
        val timer = Timer("Animation Timer", true)
        timer.schedule(visibilityDuration) {
            showMessageBar = false
        }
        onDispose {
            timer.cancel()
            timer.purge()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = if (position == MessageBarPosition.TOP)
            Arrangement.Top else Arrangement.Bottom
    ) {
        AnimatedVisibility(
            visible = messageBarState.error != null && showMessageBar
                    || messageBarState.success != null && showMessageBar,
            enter = enterAnimation,
            exit = exitAnimation
        ) {
            MessageBar(
                message = message,
                error = error,
                successIcon = successIcon,
                errorIcon = errorIcon,
                errorMaxLines = errorMaxLines,
                successMaxLines = successMaxLines,
                successContainerColor = successContainerColor,
                successContentColor = successContentColor,
                errorContainerColor = errorContainerColor,
                errorContentColor = errorContentColor,
                verticalPadding = verticalPadding,
                horizontalPadding = horizontalPadding,
            )
        }
    }
}

@Composable
internal fun MessageBar(
    message: String?,
    error: String?,
    successIcon: ImageVector,
    errorIcon: ImageVector,
    errorMaxLines: Int,
    successMaxLines: Int,
    successContainerColor: Color,
    successContentColor: Color,
    errorContainerColor: Color,
    errorContentColor: Color,
    verticalPadding: Dp,
    horizontalPadding: Dp,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (error != null) errorContainerColor
                else successContainerColor
            )
            .padding(vertical = verticalPadding)
            .padding(horizontal = horizontalPadding)
            .animateContentSize()
            .testTag(""),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(4f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector =
                if (error != null) errorIcon
                else successIcon,
                contentDescription = "Message Bar Icon",
                tint = if (error != null) errorContentColor
                else successContentColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                modifier = Modifier.testTag(tag = ""),
                text = message ?: (error ?: "نامشخص"),
                color = if (error != null) errorContentColor
                else successContentColor,
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
               // overflow = TextOverflow.Ellipsis,
               // maxLines = if (error != null) errorMaxLines else successMaxLines
            )
        }
    }
}


class MessageBarState {
    var success by mutableStateOf<String?>(null)
        private set
    var error by mutableStateOf<Exception?>(null)
        private set
    internal var updated by mutableStateOf(false)
        private set

    fun addSuccess(message: String) {
        error = null
        success = message
        updated = !updated
    }

    fun addError(exception: Exception) {
        success = null
        error = exception
        updated = !updated
    }
}

@Composable
@Preview
internal fun MessageBarPreview() {
    MessageBar(
        message = "Successfully Updated.",
        error = null,
        successIcon = Icons.Default.Check,
        errorIcon = Icons.Default.Warning,
        successMaxLines = 1,
        errorMaxLines = 1,
        successContainerColor = MaterialTheme.colorScheme.primaryContainer,
        successContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        errorContainerColor = MaterialTheme.colorScheme.errorContainer,
        errorContentColor = MaterialTheme.colorScheme.onErrorContainer,
        verticalPadding = 12.dp,
        horizontalPadding = 12.dp
    )
}

@Composable
@Preview
internal fun MessageBarErrorPreview() {
    MessageBar(
        message = null,
        error = "Internet Unavailable.",
        errorIcon = Icons.Default.Warning,
        successIcon = Icons.Default.Check,
        errorMaxLines = 1,
        successMaxLines = 1,
        successContainerColor = MaterialTheme.colorScheme.primaryContainer,
        successContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        errorContainerColor = MaterialTheme.colorScheme.errorContainer,
        errorContentColor = MaterialTheme.colorScheme.onErrorContainer,
        verticalPadding = 12.dp,
        horizontalPadding = 12.dp
    )
}
