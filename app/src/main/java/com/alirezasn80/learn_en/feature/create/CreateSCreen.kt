package com.alirezasn80.learn_en.feature.create

import android.content.ClipboardManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.app.navigation.NavigationState
import com.alirezasn80.learn_en.ui.common.UI
import com.alirezasn80.learn_en.ui.theme.Line
import com.alirezasn80.learn_en.ui.theme.SmallSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.utill.Keyboard
import com.alirezasn80.learn_en.utill.debug
import com.alirezasn80.learn_en.utill.keyboardAsState

@Composable
fun CreateScreen(
    navigationState: NavigationState, viewModel: CreateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isKeyboardOpen by keyboardAsState()
    val (focusContent, onFocusContentChange) = remember { mutableStateOf(false) }



    UI {
        Scaffold(
            bottomBar = {

                if (focusContent && isKeyboardOpen == Keyboard.Opened)
                    KeyboardBar(
                        onContentChange = { clipData ->
                            val new = state.content + clipData
                            debug("new : $new")
                            viewModel.onContentChange(new)

                        }
                    )

            }
        ) { scaffoldPadding ->
            Column(
                Modifier
                    .padding(scaffoldPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Header(R.string.create_story, navigationState::upPress)
                SmallSpacer()
                TitleSection(state.title, viewModel::onTitleChange)
                SmallSpacer()
                ContentSection(
                    value = state.content,
                    onValueChange = viewModel::onContentChange,
                    onFocusChanged = onFocusContentChange
                )
            }
        }

    }
}

@Composable
private fun KeyboardBar(onContentChange: (String) -> Unit) {

    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {

        }) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_voice_en),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.bb),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_doc_scan),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ContentSection(value: String, onValueChange: (String) -> Unit, onFocusChanged: (Boolean) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }



    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = "Content...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                onFocusChanged(focusState.isFocused)
            },
        shape = RectangleShape,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedIndicatorColor = Color.Transparent

        )

    )
}

@Composable
private fun TitleSection(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = "Title", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedIndicatorColor = Color.Transparent
        )

    )
}


@Composable
private fun Header(
    titleId: Int,
    upPress: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        IconButton(
            onClick = upPress,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = stringResource(id = titleId),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.Center)
        )

    }
}
