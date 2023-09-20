package ru.vtb24.mobilebanking.android.locator.presentation.onboarding

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.vtb24.mobilebanking.android.locator.presentation.common.NetworkImage
import ru.vtb24.mobilebanking.android.locator.presentation.theme.AppTheme
import ru.vtb24.mobilebanking.android.locator.presentation.theme.VTBLocatorTheme
import kotlin.math.max

@Composable
private fun TopicsGrid(
    topics: List<Topic>,
    modifier: Modifier = Modifier,
) {
    StaggeredGrid(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp)
    ) {
        topics.forEach { topic ->
            TopicChip(topic = topic)
        }
    }
}


private enum class SelectionState { Unselected, Selected }

/**
 * Class holding animating values when transitioning topic chip states.
 */
private class TopicChipTransition(
    cornerRadius: State<Dp>,
    selectedAlpha: State<Float>,
    checkScale: State<Float>
) {
    val cornerRadius by cornerRadius
    val selectedAlpha by selectedAlpha
    val checkScale by checkScale
}

@Composable
private fun topicChipTransition(topicSelected: Boolean): TopicChipTransition {
    val transition = updateTransition(
        targetState = if (topicSelected) SelectionState.Selected else SelectionState.Unselected,
        label = ""
    )
    val cornerRadius = transition.animateDp(label = "RadiusAnimation") { state ->
        when (state) {
            SelectionState.Unselected -> 0.dp
            SelectionState.Selected -> 28.dp
        }
    }
    val selectedAlpha = transition.animateFloat(label = "AlphaAnimation") { state ->
        when (state) {
            SelectionState.Unselected -> 0f
            SelectionState.Selected -> 0.8f
        }
    }
    val checkScale = transition.animateFloat(label = "ScaleAnimation") { state ->
        when (state) {
            SelectionState.Unselected -> 0.6f
            SelectionState.Selected -> 1f
        }
    }
    return remember(transition) {
        TopicChipTransition(cornerRadius, selectedAlpha, checkScale)
    }
}

@Composable
private fun TopicChip(topic: Topic) {
    val (selected, onSelected) = remember { mutableStateOf(false) }
    val topicChipTransitionState = topicChipTransition(selected)

    Surface(
        modifier = Modifier.padding(4.dp),
        elevation = AppTheme.elevations.card,
        shape = MaterialTheme.shapes.medium.copy(
            topStart = CornerSize(
                topicChipTransitionState.cornerRadius
            )
        )
    ) {
        Row(modifier = Modifier.toggleable(value = selected, onValueChange = onSelected)) {
            Box {
                NetworkImage(
                    url = topic.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 72.dp, height = 72.dp)
                        .aspectRatio(1f)
                )
                if (topicChipTransitionState.selectedAlpha > 0f) {
                    Surface(
                        color = AppTheme.colors.primary.copy(alpha = topicChipTransitionState.selectedAlpha),
                        modifier = Modifier.matchParentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onPrimary.copy(
                                alpha = topicChipTransitionState.selectedAlpha
                            ),
                            modifier = Modifier
                                .wrapContentSize()
                                .scale(topicChipTransitionState.checkScale)
                        )
                    }
                }
            }
            Column {
                Text(
                    text = topic.name,
                    style = AppTheme.typography.body,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
//                        Icon(
//                            painter = painterResource(R.drawable.ic_grain),
//                            contentDescription = null,
//                            modifier = Modifier
//                                .padding(start = 16.dp)
//                                .size(12.dp)
//                        )
                        Text(
                            text = topic.courses.toString(),
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val rowWidths = IntArray(rows) { 0 } // Keep track of the width of each row
        val rowHeights = IntArray(rows) { 0 } // Keep track of the height of each row

        // Don't constrain child views further, measure them with given constraints
        val placeables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(constraints)

            // Track the width and max height of each row
            val row = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = max(rowHeights[row], placeable.height)

            placeable
        }

        // Grid's width is the widest row
        val width = rowWidths.maxOrNull()?.coerceIn(constraints.minWidth, constraints.maxWidth)
            ?: constraints.minWidth
        // Grid's height is the sum of each row
        val height = rowHeights.sum().coerceIn(constraints.minHeight, constraints.maxHeight)

        // y co-ord of each row
        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i - 1] + rowHeights[i - 1]
        }
        layout(width, height) {
            // x co-ord we have placed up to, per row
            val rowX = IntArray(rows) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.place(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}

@Preview("Topic Chip")
@Composable
private fun TopicChipPreview() {
    VTBLocatorTheme {
        TopicChip(topics.first())
    }
}
