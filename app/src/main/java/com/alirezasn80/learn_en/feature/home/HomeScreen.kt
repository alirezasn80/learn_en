package com.alirezasn80.learn_en.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.app.navigation.NavigationState
import com.alirezasn80.learn_en.core.domain.entity.CategoryEntity
import com.alirezasn80.learn_en.ui.common.UI
import com.alirezasn80.learn_en.ui.theme.SmallSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.utill.Rtl
import com.alirezasn80.learn_en.utill.randomColor
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun HomeScreen(
    navigationState: NavigationState,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Close drawer
    BackHandler(drawerState.isOpen) {
        if (drawerState.isOpen)
            scope.launch { drawerState.close() }
    }

    // Show Sheet
    if (showSheet)
        BottomSheet(
            onDismiss = { showSheet = false },
            onClick = { viewModel.setSelectedCategory(it);showSheet = false }
        )

    UI {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerShape = RectangleShape,
                    drawerContainerColor = MaterialTheme.colorScheme.background,
                ) {
                    DrawerItem(label = R.string.app_name, icon = Icons.Rounded.AddCircle, onClick = {})
                }
            }
        ) {
            Column(Modifier.fillMaxSize()) {
                Header(
                    selectedLevel = state.selectedCategory.name,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onLevelClick = { showSheet = true }
                )
                LazyColumn {
                    items(state.categories) {
                        ItemSection(it)
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemSection(item: CategoryEntity) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = dimension.medium)

    ) {

        Row(Modifier.padding(vertical = dimension.medium), verticalAlignment = Alignment.CenterVertically) {
            // Number
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(randomColor()),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.categoryId.toString(), style = MaterialTheme.typography.titleSmall)
            }
            SmallSpacer()
            Column {
                Text(text = "Story ${item.categoryId}", style = MaterialTheme.typography.labelSmall)
                SmallSpacer()
                Text(text = item.title, style = MaterialTheme.typography.titleSmall)
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun Header(selectedLevel: Int, onMenuClick: () -> Unit, onLevelClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(imageVector = Icons.Rounded.Menu, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
        }
        TextButton(onClick = onLevelClick, modifier = Modifier.align(Alignment.Center)) {
            Icon(modifier = Modifier.align(Alignment.CenterVertically), imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = stringResource(id = selectedLevel),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )

        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheet(onDismiss: () -> Unit, onClick: (Category) -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        shape = MaterialTheme.shapes.small,
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Rtl {
            Column(modifier = Modifier.padding(horizontal = dimension.medium)) {
                categories.forEach {
                    Row(
                        Modifier
                            .clickable { onClick(it) }
                            .fillMaxWidth()
                            .padding(vertical = dimension.medium)) {
                        Text(text = stringResource(id = it.name))

                    }
                    Divider(Modifier.fillMaxWidth())
                }
            }
        }

    }
}

@Composable
private fun DrawerItem(
    label: Int,
    icon: Any,
    color: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(dimension.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon is ImageVector)
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = color)
        else if (icon is Painter)
            Image(painter = icon, contentDescription = null, modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit)
        SmallSpacer()
        Text(text = stringResource(id = label), maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleSmall)


    }
}