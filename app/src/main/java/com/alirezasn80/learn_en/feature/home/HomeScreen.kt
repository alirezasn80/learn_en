package com.alirezasn80.learn_en.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.app.navigation.NavigationState
import com.alirezasn80.learn_en.core.domain.entity.CategoryModel
import com.alirezasn80.learn_en.core.domain.entity.Items
import com.alirezasn80.learn_en.ui.common.UI
import com.alirezasn80.learn_en.ui.theme.SmallSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.utill.Rtl
import com.alirezasn80.learn_en.utill.createImageBitmap
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigationState: NavigationState,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Hidden, skipHiddenState = false)
    )
    val scope = rememberCoroutineScope()

    // Close drawer
    BackHandler(drawerState.isOpen) {
        if (drawerState.isOpen)
            scope.launch { drawerState.close() }
    }

    // Close Bottom Sheet
    BackHandler(bottomSheetState.bottomSheetState.isVisible) {
        if (bottomSheetState.bottomSheetState.isVisible)
            scope.launch { bottomSheetState.bottomSheetState.hide() }
    }

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
            BottomSheetScaffold(
                scaffoldState = bottomSheetState,
                sheetPeekHeight = 0.dp,
                sheetContent = {
                    BottomSheet(
                        onClick = {
                            viewModel.setSelectedLevel(it)
                            scope.launch { bottomSheetState.bottomSheetState.hide() }
                        },
                        onCreateClick = navigationState::navToCreate
                    )
                }
            ) {
                Column(Modifier.fillMaxSize()) {
                    Header(
                        selectedLevel = state.selectedSection.name,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onLevelClick = {
                            scope.launch {
                                if (bottomSheetState.bottomSheetState.isVisible)
                                    bottomSheetState.bottomSheetState.hide()
                                else

                                    bottomSheetState.bottomSheetState.expand()
                            }
                        },
                        onCreateClick = navigationState::navToCreate
                    )
                    LazyColumn {
                        if (state.selectedSection.key == "favorite") {
                            itemsIndexed(state.favorites) { index, item ->
                                FavoriteItemSection(
                                    index = index + 1,
                                    item = item,
                                    onClick = { navigationState.navToContent(item.categoryId!!, item.contentId!!) }
                                )
                            }
                        } else
                            items(state.categories) {
                                CategoryItemSection(
                                    item = it,
                                    onClick = { navigationState.navToStories(it.id, it.title) }
                                )
                            }
                    }
                }
            }

        }
    }
}

@Composable
private fun CategoryItemSection(item: CategoryModel, onClick: () -> Unit) {
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = dimension.medium)

    ) {

        Row(
            Modifier

                .fillMaxWidth()
                .padding(vertical = dimension.medium), verticalAlignment = Alignment.CenterVertically
        ) {
            // Number
            if (item.image == null)
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.id.toString(), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
                }
            else {
                Image(
                    bitmap = createImageBitmap(context, item.image),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

            }
            SmallSpacer()
            Column {
                Text(text = "Story ${item.id}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground)
                SmallSpacer()
                Text(text = item.title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())
    }
}


@Composable
private fun FavoriteItemSection(
    index: Int,
    item: Items,
    onClick: () -> Unit
) {

    Column(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = dimension.medium)

    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = dimension.medium), verticalAlignment = Alignment.CenterVertically
        ) {

            // Number
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(text = index.toString(), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
            }

            SmallSpacer()
            Column {
                Text(text = "Story ${item.contentId}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground)
                SmallSpacer()
                Text(text = item.title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())
    }
}


@Composable
private fun Header(
    selectedLevel: Int,
    onMenuClick: () -> Unit,
    onLevelClick: () -> Unit,
    onCreateClick: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
    ) {
        IconButton(onClick = onMenuClick, modifier = Modifier.align(Alignment.CenterEnd)) {
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

        IconButton(onClick = onCreateClick, modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_add_circle), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheet(
    onClick: (Section) -> Unit,
    onCreateClick: () -> Unit,
) {
    Rtl {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            sections.forEach {
                Row(
                    Modifier
                        .clickable { onClick(it) }
                        .fillMaxWidth()
                        .padding(vertical = 1.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(dimension.medium),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(id = it.name))
                    if (it is Section.Document)
                        Text(
                            text = stringResource(id = R.string.create),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onCreateClick() }
                        )

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