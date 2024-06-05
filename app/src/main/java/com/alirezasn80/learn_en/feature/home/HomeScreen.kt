package com.alirezasn80.learn_en.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.annotation.ExperimentalCoilApi
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.app.navigation.NavigationState
import com.alirezasn80.learn_en.core.domain.remote.Category
import com.alirezasn80.learn_en.feature.stories.model.Book
import com.alirezasn80.learn_en.ui.common.BaseTextButton
import com.alirezasn80.learn_en.ui.common.UI
import com.alirezasn80.learn_en.ui.common.shimmerEffect
import com.alirezasn80.learn_en.ui.theme.ExitRed
import com.alirezasn80.learn_en.ui.theme.LargeSpacer
import com.alirezasn80.learn_en.ui.theme.MediumSpacer
import com.alirezasn80.learn_en.ui.theme.Red100
import com.alirezasn80.learn_en.ui.theme.SmallSpacer
import com.alirezasn80.learn_en.ui.theme.ThemeViewModel
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.utill.CoilImage
import com.alirezasn80.learn_en.utill.Key
import com.alirezasn80.learn_en.utill.Ltr
import com.alirezasn80.learn_en.utill.Progress
import com.alirezasn80.learn_en.utill.Reload
import com.alirezasn80.learn_en.utill.User
import com.alirezasn80.learn_en.utill.openAppInCafeBazaar
import com.alirezasn80.learn_en.utill.openBazaarComment
import com.alirezasn80.learn_en.utill.openGmail
import com.alirezasn80.learn_en.utill.rememberPermissionState
import com.alirezasn80.learn_en.utill.shareText
import com.alirezasn80.learn_en.utill.showToast
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@Composable
fun HomeScreen(
    navigationState: NavigationState,
    themeViewModel: ThemeViewModel,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var reqToExit by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current
    var showTabs by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val permissionState = rememberPermissionState(
        onGranted = {},
        onDenied = {}
    )

    // Check to show witch dialog
    when (state.dialogKey) {
        HomeDialogKey.AskRate -> {
            viewModel.resetOpenAppCounter()
            AskRateDialog(
                reqToExit = reqToExit,
                onDismissRequest = {
                    reqToExit = false
                    viewModel.setDialogKey(HomeDialogKey.Hide)
                },
                onYesClick = {
                    viewModel.setDialogKey(HomeDialogKey.Hide)
                    viewModel.hideCommentItem(Key.POSITIVE)
                    context.showToast(R.string.support_by_5_star)
                    context.openBazaarComment()
                },
                onNoClick = {
                    viewModel.setDialogKey(HomeDialogKey.BadRate)
                    viewModel.hideCommentItem(Key.NEGATIVE)
                },
                onExitClick = {
                    exitProcess(0)
                }
            )
        }

        HomeDialogKey.BadRate -> {
            BadRateDialog(
                onDismissRequest = {
                    viewModel.setDialogKey(HomeDialogKey.Hide)
                },
                action = {
                    viewModel.setDialogKey(HomeDialogKey.Hide)
                    AppMetrica.reportError("BadRate", it)
                    context.showToast(R.string.your_text_sent)
                }
            )
        }

        HomeDialogKey.Hide -> Unit
    }

    //Back With Ask Comment
    BackHandler(state.openAppCount >= 3 && state.showComment) {
        reqToExit = true
        viewModel.setDialogKey(HomeDialogKey.AskRate)
    }


    // Check Notification Permission
    LaunchedEffect(key1 = Unit) {
        if (state.showNotificationAlert) {
            permissionState.requestNotification()
            viewModel.hideNotificationAlert()
        }
    }

    // Close drawer
    BackHandler(drawerState.isOpen) {
        if (drawerState.isOpen)
            scope.launch { drawerState.close() }
    }

    // Check Reload
    LaunchedEffect(Unit) {

        if (Reload.local || Reload.favorite) {
            viewModel.reloadData()
        }

    }

    UI(uiComponent = viewModel.uiComponents, progress = viewModel.progress["bazaar"]) {

        if (showTabs) {
            BottomSheet(
                onDismiss = { showTabs = false },
                onClick = {
                    if (it != state.selectedTab) {
                        viewModel.setSelectedTab(it)
                    }
                    showTabs = false
                },
                onCreateClick = navigationState::navToCreate
            )
        }


        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerShape = RectangleShape,
                    drawerContainerColor = MaterialTheme.colorScheme.background,
                ) {
                    HeaderDrawer(
                        isDarkTheme = themeViewModel.isDarkTheme.value,
                        onChangeTheme = themeViewModel::toggleTheme,
                    )

                    if (state.needUpdate) {
                        UpdateSection(onClick = {
                            context.openAppInCafeBazaar()
                            viewModel.hideNeedUpdate()
                        })
                    }


                    // VIP User
                    DrawerItem(
                        label = R.string.vip_user,
                        icon = painterResource(id = R.drawable.img_vip),
                        onClick = {
                            scope.launch { drawerState.close() }

                            if (User.isVipUser)
                                context.showToast(R.string.you_now_vip)
                            else
                                navigationState.navToPayment("DRAWER")
                        }
                    )

                    // Check Subscribe
                    DrawerItem(
                        label = R.string.check_subscribe_status,
                        icon = painterResource(id = R.drawable.img_check_subscribe),
                        onClick = {
                            scope.launch { drawerState.close() }
                            viewModel.checkSubscribeStatus()
                        }
                    )


                    // Flash Card
                    DrawerItem(
                        label = R.string.flash_card,
                        icon = painterResource(id = R.drawable.img_flash_card),
                        onClick = {
                            scope.launch { drawerState.close() }
                            navigationState.navToFlashCard()
                        }
                    )

                    // Invite Friends
                    DrawerItem(
                        label = R.string.invite_friends,
                        icon = ImageVector.vectorResource(R.drawable.ic_person_add),
                        onClick = {
                            scope.launch { drawerState.close() }
                            context.shareText(R.string.send_app_to_other)
                        }
                    )

                    DrawerItem(
                        label = R.string.submit_comment,
                        icon = ImageVector.vectorResource(R.drawable.ic_comment),
                        onClick = {
                            scope.launch { drawerState.close() }
                            viewModel.setDialogKey(HomeDialogKey.AskRate)
                        }
                    )

                    // Support
                    DrawerItem(
                        label = R.string.support,
                        icon = ImageVector.vectorResource(R.drawable.ic_support),
                        color = Color(0xFFB08876),
                        onClick = {
                            scope.launch { drawerState.close() }
                            try {
                                context.openGmail(false)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    )

                    DrawerItem(
                        label = R.string.about_us,
                        icon = ImageVector.vectorResource(R.drawable.ic_about),
                        onClick = {
                            navigationState.navToAboutUs()
                        }
                    )

                }
            }
        ) {
            Column(Modifier.fillMaxSize()) {
                Header(
                    needUpdate = state.needUpdate,
                    selectedTab = state.selectedTab.name,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onTabClick = {
                        showTabs = !showTabs
                    },
                    onCreateClick = navigationState::navToCreate
                )
                Ltr {

                    when {

                        viewModel.progress[""] is Progress.Loading -> {
                            LoadingLayout()
                        }

                        /*state.selectedTab is Tab.Favorite -> {

                            if (state.favorites.isEmpty()) {
                                EmptyLayout()
                            } else {
                                LazyColumn {
                                    itemsIndexed(state.favorites) { index, item ->
                                        FavoriteItemSection(
                                            index = index + 1,
                                            book = item,
                                            onClick = { navigationState.navToReader(item, "lock") }
                                        )
                                    }
                                }
                            }

                        }*/

                        state.selectedTab is Tab.Default -> {

                            if (state.categories.isEmpty()) {
                                EmptyLayout()
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    contentPadding = PaddingValues(12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    items(state.categories) {
                                        CategoryItemSection(
                                            isLastRead = it.id == state.lastReadCategory,
                                            item = it,
                                            onClick = {
                                                viewModel.saveAsLastRead(it.id)
                                                navigationState.navToBooks(it.id, it.name, "0")
                                            }
                                        )
                                    }

                                }
                            }
                        }

                        state.selectedTab is Tab.Local -> {
                            if (state.localCategories.isEmpty()) {
                                EmptyLayout()
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    contentPadding = PaddingValues(12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    items(state.localCategories) {
                                        CategoryItemSection(
                                            isLastRead = it.id == state.lastReadCategory,
                                            item = it,
                                            onClick = {
                                                viewModel.saveAsLastRead(it.id)
                                                navigationState.navToBooks(it.id, it.name, "1")
                                            }
                                        )
                                    }

                                }
                            }
                        }

                    }


                }

            }
        }
    }
}

@Composable
private fun UpdateSection(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val degree by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotate"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotate"
    )
    Row(
        Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(dimension.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box {
            Image(
                painter = painterResource(id = R.drawable.img_update),
                contentDescription = null, modifier = Modifier
                    .size(24.dp)
                    .rotate(degree),
                contentScale = ContentScale.Fit
            )

            Icon(
                contentDescription = null,
                painter = painterResource(id = R.drawable.ic_circle),
                modifier = Modifier
                    .size(13.dp)
                    .offset(x = (-5).dp, y = (-5).dp),
                tint = Red100
            )

        }

        SmallSpacer()
        Text(
            text = stringResource(id = R.string.update_app),
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .scale(scale)
        )

    }
}


@Composable
private fun HeaderDrawer(
    isDarkTheme: Boolean,
    onChangeTheme: () -> Unit,
) {
    Box(Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(dimension.medium)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.img_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(85.dp)
                            .clip(CircleShape)
                    )

                    if (User.isVipUser) {
                        Icon(
                            painter = painterResource(id = R.drawable.img_vip2),
                            contentDescription = null,
                            modifier = Modifier
                                .size(45.dp)
                                .align(Alignment.TopStart)
                                .offset(x = (-10).dp, y = (-10).dp)
                                .alpha(0.9f),
                            tint = Color.Unspecified
                        )
                    }
                }


                IconButton(
                    onClick = onChangeTheme,
                    colors = IconButtonDefaults.iconButtonColors()
                ) {
                    AnimatedContent(
                        targetState = isDarkTheme,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(700))
                        }, label = ""
                    ) {
                        Icon(
                            painter = painterResource(id = if (it) R.drawable.ic_sun else R.drawable.ic_moon),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                }
            }

            SmallSpacer()

            Column {
                Text(text = stringResource(id = R.string.enlish_stories))
                SmallSpacer()
                Text(text = stringResource(id = R.string.learn_english_with_story), style = MaterialTheme.typography.labelSmall)
            }


        }
    }


}

@Composable
private fun LoadingLayout() {
    repeat(15) {
        Column(
            Modifier
                .fillMaxWidth()
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
                        .background(MaterialTheme.colorScheme.primary)
                        .shimmerEffect(),
                )

                SmallSpacer()
                Column {
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(6.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect()
                    )
                    SmallSpacer()
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(8.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect()
                    )
                }
            }
            Divider(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun EmptyLayout() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.vector_no_data),
            contentDescription = null,
            modifier = Modifier.padding(dimension.large)
        )
    }
}


@OptIn(ExperimentalCoilApi::class)
@Composable
private fun CategoryItemSection(
    isLastRead: Boolean,
    item: Category,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        Modifier
            .padding(dimension.small)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Number
        if (item.cover == null)
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shadow(1.dp)
                    .zIndex(1f)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.id.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
            }
        else {
            CoilImage(
                modifier = Modifier
                    .aspectRatio(1f)
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shadow(1.dp)
                    .zIndex(1f), data = item.cover,
                contentScale = ContentScale.Crop
            )
        }

        SmallSpacer()

        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isLastRead) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            fontWeight = if (isLastRead) FontWeight.Bold else FontWeight.Normal
        )


    }
}


@Composable
private fun FavoriteItemSection(
    index: Int,
    book: Book,
    onClick: () -> Unit
) {

    Column(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surface)
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(dimension.medium), verticalAlignment = Alignment.CenterVertically
        ) {

            // Number
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(text = index.toString(), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSecondary)
            }

            SmallSpacer()
            Column {
                Text(text = "Story ${book.bookId}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground)
                SmallSpacer()
                Text(text = book.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
            }
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp), color = MaterialTheme.colorScheme.background
        )
    }
}


@Composable
private fun Header(
    needUpdate: Boolean,
    selectedTab: Int,
    onMenuClick: () -> Unit,
    onTabClick: () -> Unit,
    onCreateClick: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
    ) {
        Box {
            IconButton(onClick = onMenuClick, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(imageVector = Icons.Rounded.Menu, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            }

            if (needUpdate)
                Icon(
                    painter = painterResource(id = R.drawable.ic_circle),
                    contentDescription = null,
                    tint = Red100,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .size(10.dp)
                )
        }

        TextButton(onClick = onTabClick, modifier = Modifier.align(Alignment.Center)) {
            Icon(modifier = Modifier.align(Alignment.CenterVertically), imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = stringResource(id = selectedTab),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )

        }

        IconButton(onClick = onCreateClick, modifier = Modifier.align(Alignment.CenterEnd)) {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_circle_add), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheet(
    onDismiss: () -> Unit,
    onClick: (Tab) -> Unit,
    onCreateClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 40.dp)
        ) {
            tabs.forEach {
                Column {
                    Row(
                        Modifier
                            .clickable { onClick(it) }
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(dimension.medium),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(id = it.name), color = MaterialTheme.colorScheme.onBackground)
                        if (it is Tab.Local)
                            Text(
                                text = stringResource(id = R.string.create),
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.clickable { onCreateClick() }
                            )

                    }
                    Divider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)
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


@Composable
private fun AskRateDialog(
    reqToExit: Boolean,
    onDismissRequest: () -> Unit,
    onYesClick: () -> Unit,
    onNoClick: () -> Unit,
    onExitClick: () -> Unit,
) {

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
                .padding(dimension.medium),
        ) {

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_comment),
                    contentDescription = "Comment", tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(30.dp)
                )
                SmallSpacer()
                Text(
                    text = stringResource(id = R.string.submit_comment),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            SmallSpacer()

            Text(text = stringResource(id = R.string.dialog_text_satisfied), color = MaterialTheme.colorScheme.onSurface)

            LargeSpacer()


            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {


                if (reqToExit) {
                    BaseTextButton(
                        text = R.string.exit,
                        contentColor = ExitRed,
                        onclick = onExitClick
                    )
                } else
                    MediumSpacer()

                Row(verticalAlignment = Alignment.CenterVertically) {


                    BaseTextButton(
                        text = R.string.no,
                        onclick = onNoClick
                    )
                    SmallSpacer()
                    BaseTextButton(
                        text = R.string.yes,
                        onclick = onYesClick
                    )

                }


            }
        }
    }


}

@Composable
private fun BadRateDialog(
    onDismissRequest: () -> Unit,
    action: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
                .padding(dimension.medium),
        ) {

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_comment),
                    contentDescription = "", tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(30.dp)
                )
                SmallSpacer()
                Text(
                    text = stringResource(id = R.string.report),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }


            SmallSpacer()

            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.report_text), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }
            )

            LargeSpacer()

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                BaseTextButton(text = R.string.send, onclick = { action(text) })
                SmallSpacer()
                BaseTextButton(text = R.string.cancel, onclick = onDismissRequest)
            }
        }
    }
}


/*
*
*
                                Column(
                                    Modifier
                                        .padding(12.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    state.categories.chunked(COLUMN_COUNT).forEach {

                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            var spaceUI = 0
                                            if (it.size < COLUMN_COUNT) {
                                                spaceUI = COLUMN_COUNT - it.size
                                            }
                                            it.forEach {
                                                CategoryItemSection(
                                                    isLastRead = it.id == state.lastReadCategory,
                                                    item = it,
                                                    onClick = {
                                                        viewModel.saveAsLastRead(it.id)
                                                        navigationState.navToStories(it.id, it.title)
                                                    }
                                                )
                                            }
                                            if (spaceUI != 0) {
                                                repeat(spaceUI) {
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .padding(dimension.small)
                                                    )
                                                }
                                            }

                                        }
                                    }


                                }


*
* */