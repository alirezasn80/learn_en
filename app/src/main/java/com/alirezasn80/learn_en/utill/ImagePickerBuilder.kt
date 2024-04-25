package com.alirezasn80.learn_en.utill

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.ui.theme.LargeSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@Composable
fun rememberImagePickerBuilder(
    context: Context,
    onSelectedImage: (uri: Uri) -> Unit
): ImagePickerBuilder {
    var capturedUrl by remember { mutableStateOf(Uri.EMPTY) }
    var showDialog by remember { mutableStateOf(false) }


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { result ->
            result?.let(onSelectedImage)
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess)
            onSelectedImage(capturedUrl)
    }

    if (showDialog) {
        ChooseDialog(
            openGallery = {
                galleryLauncher.launch("image/*")
                showDialog = false
            },
            openCamera = {
                cameraLauncher.launch(context.createImageFile { capturedUrl = it })
                showDialog = false
            },
            dismiss = { showDialog = false }
        )
    }

    return remember { ImagePickerBuilder(showDialog = { showDialog = true }) }
}

class ImagePickerBuilder(
    private val showDialog: () -> Unit

) {
    fun launchPicker() {
        showDialog()
    }
}

//-------------------------------------------

private fun Context.createImageFile(onResult: (Uri) -> Unit): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val file = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    val uri = FileProvider.getUriForFile(Objects.requireNonNull(this), "com.alirezasn80.learn_en" + ".provider", file)
    onResult(uri)
    return uri
}

@Composable
private fun ChooseDialog(
    openGallery: () -> Unit,
    openCamera: () -> Unit,
    dismiss: () -> Unit
) {
    Dialog(onDismissRequest = dismiss) {

        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(150.dp)
                .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.small)
                .padding(dimension.medium)
                .clip(MaterialTheme.shapes.small),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier
                    .padding(dimension.small)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { openGallery() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_gallery), contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                Text(text = "گالری", color = MaterialTheme.colorScheme.onBackground)
            }

            LargeSpacer()

            Column(
                Modifier
                    .padding(dimension.small)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { openCamera() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_camera), contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                Text(text = "دوربین", color = MaterialTheme.colorScheme.onBackground)
            }
        }

    }
}