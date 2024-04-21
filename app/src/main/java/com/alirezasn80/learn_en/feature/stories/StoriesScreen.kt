package com.alirezasn80.learn_en.feature.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.app.navigation.NavigationState
import com.alirezasn80.learn_en.core.domain.entity.Items
import com.alirezasn80.learn_en.ui.common.UI
import com.alirezasn80.learn_en.ui.theme.SmallSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.utill.Ltr
import com.alirezasn80.learn_en.utill.User


@Composable
fun StoriesScreen(
    navigationState: NavigationState,
    viewmodel: StoriesViewModel = hiltViewModel()
) {
    val state by viewmodel.state.collectAsStateWithLifecycle()

    UI {
        Column(Modifier.fillMaxSize()) {
            Header(state.title, upPress = navigationState::upPress)
            if (!User.isVipUser)
                FreeTip()

            Ltr {
                LazyColumn {
                    itemsIndexed(state.items) { index, item ->
                        val isTrial = index <= 1 || User.isVipUser
                        ItemSection(
                            isFree = isTrial,
                            index = index,
                            item = item,
                            onClick = { navigationState.navToContent(item.categoryId!!, item.contentId!!, if (isTrial) "trial" else "lock") }
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun FreeTip() {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            .padding(dimension.small)
    ) {
        Text(text = stringResource(id = R.string.tip_free_state))
    }
}


@Composable
private fun Header(title: String, upPress: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        IconButton(onClick = upPress, modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.Center)
        )

    }
}

@Composable
private fun ItemSection(
    isFree: Boolean,
    index: Int,
    item: Items,
    onClick: () -> Unit
) {
    /*val infiniteTransition = rememberInfiniteTransition(label = "")
    val color by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.primary, targetValue = MaterialTheme.colorScheme.secondary,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )*/
    Column(
        Modifier
            .fillMaxWidth()
            .background(if (isFree) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.2f))
    ) {
        Row(
            Modifier
                .clickable { onClick() }
                .fillMaxWidth()
                .padding(dimension.medium), verticalAlignment = Alignment.CenterVertically) {
            // Number
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (index + 1).toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
            SmallSpacer()
            Column {
                Text(text = "Story ${item.contentId}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground)
                SmallSpacer()
                Text(
                    text = item.title, style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp), color = MaterialTheme.colorScheme.background
        )
    }
}
