package com.alirezasn80.learn_en.feature.create

import android.content.Intent
import android.os.Build
import android.speech.RecognizerIntent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.app.navigation.NavigationState
import com.alirezasn80.learn_en.ui.common.UI
import com.alirezasn80.learn_en.ui.theme.MediumSpacer
import com.alirezasn80.learn_en.ui.theme.SmallSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.utill.Destination
import com.alirezasn80.learn_en.utill.Keyboard
import com.alirezasn80.learn_en.utill.Ltr
import com.alirezasn80.learn_en.utill.User
import com.alirezasn80.learn_en.utill.keyboardAsState
import com.alirezasn80.learn_en.utill.rememberImagePickerBuilder
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CreateScreen(
    navigationState: NavigationState,
    viewModel: CreateViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isKeyboardOpen by keyboardAsState()
    val (focusContent, onFocusContentChange) = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var createCategoryDialog by remember { mutableStateOf(false) }
    val destination = viewModel.destination
    val bottomSheetScaffold = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Hidden, skipHiddenState = false)
    )
    val keyboardController = LocalSoftwareKeyboardController.current


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

    val imagePickerBuilder = rememberImagePickerBuilder(context = context) {
        //viewModel.processImageUri(it)
    }

    /*val launcherGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.processImageUri(uri) }
    }*/

    if (createCategoryDialog) {
        CreateCategoryDialog(
            onDismiss = { createCategoryDialog = false },
            onCreateCategory = {
                if (it.isNotBlank()) {
                    createCategoryDialog = false
                    viewModel.createCategory(it)
                }

            }
        )
    }


    // Close Bottom Sheet
    BackHandler(bottomSheetScaffold.bottomSheetState.isVisible) {
        if (bottomSheetScaffold.bottomSheetState.isVisible)
            scope.launch { bottomSheetScaffold.bottomSheetState.hide() }
    }


    LaunchedEffect(isKeyboardOpen) {
        if (isKeyboardOpen == Keyboard.Opened) {
            scope.launch { bottomSheetScaffold.bottomSheetState.hide() }
        }
    }

    LaunchedEffect(destination) {
        when (destination) {
            Destination.Back -> navigationState.upPress()
            else -> Unit
        }
    }
    UI(uiComponent = viewModel.uiComponents) {
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffold,
            sheetPeekHeight = 0.dp,
            sheetContainerColor = MaterialTheme.colorScheme.primary,
            sheetContent = {

                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    SheetCell(
                        title = stringResource(id = R.string.create_category),
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = { createCategoryDialog = true }
                    )
                    state.createdCategories.forEach {
                        //todo()
                      //  SheetCell(title = it.title, onClick = { viewModel.createStory(it.id) })
                    }
                }


            }
        ) {
            Scaffold(
                modifier = Modifier.padding(it),
                bottomBar = {

                    if (focusContent && isKeyboardOpen == Keyboard.Opened)
                        KeyboardBar(
                            onEnSttClick = {
                                enSttResult.launch(it)
                            },
                            onFaToEnSttClick = { faToEnResult.launch(it) },
                            //onScanImgClick = { imagePickerBuilder.launchPicker() }
                        )

                }
            ) { scaffoldPadding ->
                Column(
                    Modifier
                        .padding(scaffoldPadding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Header(
                        titleId = R.string.create_story,
                        upPress = navigationState::upPress,
                        onSaveClick = {
                            keyboardController?.hide()
                            if (User.isVipUser)
                                validation(
                                    title = state.title,
                                    content = state.content.text,
                                    onSuccess = {
                                        scope.launch { bottomSheetScaffold.bottomSheetState.expand() }
                                    },
                                    onFailed = { error ->
                                        viewModel.setMessageBySnackbar(error)
                                    }
                                )
                            else
                                navigationState.navToPayment("CREATE_STORY")
                        }
                    )

                    Ltr {
                        SmallSpacer()
                        TitleSection(state.title, viewModel::onTitleChange)
                        SmallSpacer()
                        Text(text = "${5000 - state.content.text.length}")
                        ContentSection(
                            value = state.content,
                            onValueChange = viewModel::onContentChange,
                            onFocusChanged = { isFocus ->
                                onFocusContentChange(isFocus)
                            }
                        )
                    }

                }
            }
        }


    }
}


fun validation(
    title: String,
    content: String,
    onSuccess: () -> Unit,
    onFailed: (Int) -> Unit
) {
    if (title.trim().isBlank()) {
        onFailed(R.string.blank_title)
    } else if (content.trim().isBlank()) {
        onFailed(R.string.blank_content)
    } else {
        onSuccess()
    }
}

@Composable
fun CreateCategoryDialog(onDismiss: () -> Unit, onCreateCategory: (String) -> Unit) {
    var categoryName by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.small)
                .padding(dimension.medium), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.new_category),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            SmallSpacer()
            Text(
                text = stringResource(id = R.string.enter_category_name),
                color = MaterialTheme.colorScheme.onBackground
            )
            SmallSpacer()
            TextField(value = categoryName, onValueChange = { if (it.length <= 35) categoryName = it }, maxLines = 1)
            MediumSpacer()
            Row(Modifier.fillMaxWidth()) {
                Button(onClick = { onCreateCategory(categoryName) }, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.confirm))
                }
                SmallSpacer()
                Button(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        }
    }
}

@Composable
fun SheetCell(
    title: String,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(dimension.medium),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = color)
    }
}

@Composable
private fun KeyboardBar(
    onEnSttClick: (Intent) -> Unit,
    onFaToEnSttClick: (Intent) -> Unit,
    // onScanImgClick: () -> Unit,
) {

    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        // horizontalArrangement = Arrangement.SpaceBetween
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

        SmallSpacer()

        IconButton(
            onClick = {
                val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR")
                onFaToEnSttClick(speechIntent)
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.voice_fa_en),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        /*IconButton(onClick = onScanImgClick) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_doc_scan),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }*/

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
        onValueChange = {
            if (it.text.length <= 5000)
                onValueChange(it)
        },
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
        ),
        maxLines = 1,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)

    )
}


@Composable
private fun Header(
    titleId: Int,
    upPress: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = upPress
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = stringResource(id = titleId),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )

        IconButton(
            onClick = {
                onSaveClick()
            },
        ) {
            Icon(
                imageVector = Icons.Rounded.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary
            )
        }


    }
}
