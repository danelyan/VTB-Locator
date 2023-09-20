package ru.vtb24.mobilebanking.android.locator.presentation.onboarding

import android.app.Activity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.vtb24.mobilebanking.android.locator.presentation.common.Screen
import ru.vtb24.mobilebanking.android.locator.presentation.theme.AppTheme
import ru.vtb24.mobilebanking.android.locator.presentation.theme.VTBLocatorTheme

@Composable
fun OnBoardingScreen(
    navController: NavHostController,
) {
    OnBoardingView {
        navController.navigate(Screen.LoginScreen.route)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingView(
    onNextClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppTheme.colors.primary
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val context = LocalContext.current as Activity
            context.window.statusBarColor = AppTheme.colors.primary.toArgb()
            context.window.navigationBarColor = AppTheme.colors.primary.toArgb()

            val scope = rememberCoroutineScope()

            val items = OnBoardingItem.get()

            val pagerState = rememberPagerState(
                initialPage = 0,
                initialPageOffsetFraction = 0f
            ) {
                items.size
            }

            HorizontalPager(
//                count = items.size,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.8f),
                state = pagerState,
            ) { page ->
                OnboardingPage(item = items[page])
            }
            Column(
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 16.dp)
                    .fillMaxSize()
                    .weight(0.2f),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BottomSection(size = items.size, index = pagerState.currentPage) {
                    if (pagerState.currentPage + 1 < items.size) {
                        scope.launch {
                            pagerState.scrollToPage(pagerState.currentPage + 1)
                        }

                    } else {
                        onNextClick.invoke()
                    }
                }
            }

        }
    }
}


@Composable
fun OnboardingPage(item: OnBoardingItem) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = item.image),
            contentDescription = stringResource(id = item.title),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = item.title),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = item.text),
            color = Color.White,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BottomSection(
    size: Int,
    index: Int,
    onNextClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // indicators
        Indicators(size = size, index = index)

        // next button
        FloatingActionButton(
            onClick = onNextClicked,
            modifier = Modifier.align(CenterEnd),
            contentColor = Color.White
        ) {
            Icon(Icons.Outlined.KeyboardArrowRight, null)
        }
    }
}


@Composable
fun BoxScope.Indicators(size: Int, index: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.align(Alignment.CenterStart)
    ) {
        repeat(size) {
            Indicator(isSelected = it == index)
        }
    }
}

@Composable
fun Indicator(isSelected: Boolean) {

    val width = animateDpAsState(
        targetValue = if (isSelected) 25.dp else 10.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = ""
    )

    Box(
        modifier = Modifier
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(if (isSelected) AppTheme.colors.secondary else Color.White)
    ) {

    }
}

@Preview
@Composable
fun OnBoardingViewPreview() {
    VTBLocatorTheme {
        OnBoardingView {}
    }
}
