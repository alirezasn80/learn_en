package com.alirezasn80.learn_en.feature.offline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.app.navigation.NavigationState
import com.alirezasn80.learn_en.ui.common.BaseButton
import com.alirezasn80.learn_en.ui.theme.LargeSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.utill.isOnline

@Composable
fun OfflineScreen(navigationState: NavigationState) {
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimension.medium), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_offline),
            contentDescription = "Offline",
            modifier = Modifier
                .size(70.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        LargeSpacer()
        Text(
            text = stringResource(id = R.string.youre_offline),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall
        )
        LargeSpacer()
        BaseButton(text = R.string.try_again) {
            if (isOnline(context))
                navigationState.navToSplash()
        }
    }
}