package com.alirezasn80.learn_en.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.alirezasn80.learn_en.ui.theme.ExtraSmallSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.utill.toStr


@Composable
fun PopUpMenu(
    modifier: Modifier = Modifier,
    mainIcon: Any,
    lockItemIndex: Int = -1,
    titleMenuItems: List<Any>,
    iconMenuItems: List<Any>? = null,
    mainTitleStyle: TextStyle = MaterialTheme.typography.titleSmall,
    menuItemTitleStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    iconColor: Color = MaterialTheme.colorScheme.onPrimary,
    selectedItem: (id: Int) -> Unit,
) {

    var mainText: String? = null
    var mainImageVector: ImageVector? = null
    var expanded by remember { mutableStateOf(false) }

    when (mainIcon) {
        is ImageVector -> mainImageVector = mainIcon
        is String -> mainText = mainIcon
        is Int -> mainImageVector = ImageVector.vectorResource(id = mainIcon)
        else -> mainText = ""
    }

    Box(modifier) {
        if (mainImageVector != null)
            Icon(
                imageVector = mainImageVector,
                contentDescription = mainImageVector.name,
                tint = iconColor,
                modifier = Modifier.clickable(
                    role = Role.Button,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 24.dp)
                ) {
                    expanded = !expanded
                }
            )
        else if (mainText != null)
            Text(
                text = mainText,
                style = mainTitleStyle,
                modifier = Modifier
                    .clickable(
                        role = Role.Button,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false, radius = 24.dp)
                    ) {
                        expanded = !expanded
                    },
                color = iconColor
            )
        else
            return


        DropdownMenu(modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            expanded = expanded, onDismissRequest = {
                expanded = false
            }) {

            titleMenuItems.forEachIndexed { index, item ->

                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expanded = false
                                selectedItem(index)
                            }
                            .blur(if (lockItemIndex == index) 0.5.dp else 0.dp)
                            .background(
                                if (lockItemIndex == index) Color.LightGray.copy(alpha = 0.1f) else
                                    MaterialTheme.colorScheme.surface
                            )
                            .padding(horizontal = dimension.medium)

                    ) {
                        if (iconMenuItems != null) {

                            val itemImageVector = when (iconMenuItems[index]) {
                                is ImageVector -> iconMenuItems[index] as ImageVector
                                is Int -> ImageVector.vectorResource(id = iconMenuItems[index] as Int)
                                else -> Icons.Default.Done
                            }

                            Icon(
                                imageVector = itemImageVector,
                                contentDescription = itemImageVector.name,
                                modifier = Modifier.size(24.dp)
                            )

                            ExtraSmallSpacer()
                        }

                        Text(
                            text = item.toStr(),
                            modifier = Modifier
                                .padding(dimension.small),
                            style = menuItemTitleStyle
                        )
                    }

                    if (index == lockItemIndex)
                        Icon(imageVector = Icons.Rounded.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.CenterEnd))
                }

            }
        }

    }

}

