package com.alirezasn80.learn_en.feature.create

import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.app.navigation.NavigationState
import com.alirezasn80.learn_en.ui.common.UI
import com.alirezasn80.learn_en.ui.theme.SmallSpacer
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
    val context = LocalContext.current


    val enSttResult = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { data ->
        val result = data.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        if (result != null) {
            val text = result.joinToString()
            val newResult = state.content.copy(state.content.text.plus(text))
            viewModel.onContentChange(newResult, true)
        }
    }

    val faToEnResult = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { data ->
        val result = data.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        if (result != null) {
            val text = result.joinToString()
            viewModel.onTranslatedContent(text)
        }
    }

    val launcherGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.processImageUri(uri) }

    }



    UI {
        Scaffold(
            bottomBar = {

                if (focusContent && isKeyboardOpen == Keyboard.Opened)
                    KeyboardBar(
                        onEnSttClick = { enSttResult.launch(it) },
                        onFaToEnSttClick = { faToEnResult.launch(it) },
                        onScanImgClick = { launcherGallery.launch("image/*") }
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
private fun KeyboardBar(
    onEnSttClick: (Intent) -> Unit,
    onFaToEnSttClick: (Intent) -> Unit,
    onScanImgClick: () -> Unit,
) {

    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {
                val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                onEnSttClick(speechIntent)
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_voice_en),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        IconButton(
            onClick = {
                val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR")
                onFaToEnSttClick(speechIntent)
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.bb),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        IconButton(onClick = onScanImgClick) {
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
private fun ContentSection(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit, onFocusChanged: (Boolean) -> Unit) {
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
