package com.alirezasn80.learn_en.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.alirezasn80.learn_en.ui.theme.MediumSpacer


@Composable
fun EmptyLayout(
    icon: Int,
    body: Int
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = ImageVector.vectorResource(id = icon), contentDescription = "", modifier = Modifier.size(70.dp), tint = Color.Unspecified)
        MediumSpacer()
        Text(text = stringResource(id = body))
    }
}
