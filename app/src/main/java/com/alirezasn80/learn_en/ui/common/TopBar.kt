package com.alirezasn80.learn_en.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.alirezasn80.learn_en.ui.theme.MediumSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.utill.toStr

@Composable
fun BaseTopBar(
    title: Any,
    upPress: () -> Unit,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.background
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(vertical = dimension.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowForward,
            contentDescription = "Up Press",
            tint = contentColor,
            modifier = Modifier
                .padding(start = dimension.medium)
                .size(24.dp)
                .clickable(
                    role = Role.Button,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 24.dp - 24.dp / 3)
                ) { upPress() }
        )
        MediumSpacer()
        Text(text = title.toStr(), style = MaterialTheme.typography.titleLarge, color = contentColor)
    }

}