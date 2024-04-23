package com.alirezasn80.learn_en.feature.aboutus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.ui.common.BaseTopBar
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.utill.getVersionName

@Composable
fun AboutUsScreen(upPress: () -> Unit) {
    Scaffold(
        topBar = {
            BaseTopBar(title = R.string.aboutus, upPress = upPress)
        },
        bottomBar = {
            val context = LocalContext.current
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimension.small), horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "نسخه ${context.getVersionName()}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.colorScheme.background)
                .padding(dimension.medium)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(id = R.string.aboutus_desc),
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 30.sp)
            )
        }
    }
}