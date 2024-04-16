package com.alirezasn80.learn_en.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BaseButton(
    modifier: Modifier = Modifier,
    text: Any,
    shape: CornerBasedShape = MaterialTheme.shapes.small,
    style: TextStyle = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
    containerColor: Color = MaterialTheme.colorScheme.primary,
    paddingValues: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
    contentColor: Color = if (containerColor == MaterialTheme.colorScheme.primary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
    enabled: Boolean = true,
    onclick: () -> Unit,
) {
    val textButton = when (text) {
        is Int -> stringResource(id = text)
        is String -> text
        else -> ""
    }
    Button(
        onClick = onclick,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = modifier,
        enabled = enabled
    ) {
        Text(
            text = textButton,
            style = style,
            modifier = Modifier.padding(paddingValues)
        )
    }
}


@Composable
fun BaseTextButton(
    modifier: Modifier = Modifier,
    text: Any,
    contentColor: Color = MaterialTheme.colorScheme.secondary,
    fontSize: TextUnit = TextUnit.Unspecified,
    onclick: () -> Unit,
) {
    val textButton = when (text) {
        is Int -> stringResource(id = text)
        is String -> text
        else -> ""
    }

    TextButton(onClick = onclick, modifier = modifier) {
        Text(
            text = textButton,
            style = MaterialTheme.typography.titleSmall.copy(color = contentColor),
            fontSize = fontSize
        )

    }
}