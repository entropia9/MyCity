package com.example.mycity

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mycity.ui.ExpandedStartScreen
import com.example.mycity.ui.MyCityViewModel
import com.example.mycity.ui.PickCategoryScreen
import com.example.mycity.ui.PickPlaceScreen
import com.example.mycity.ui.PlaceScreen
import com.example.mycity.ui.StartScreen
import com.example.mycity.ui.theme.MyCityTheme
import com.example.mycity.utils.WindowStateContentType


enum class MyCityScreen {
    Start, CategoryList, PlacesList, Place

}

@Composable
fun MyCityApp(
    windowSize: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController(),
    viewModel: MyCityViewModel = viewModel()
) {
    val contentType = when (windowSize) {
        WindowWidthSizeClass.Expanded -> WindowStateContentType.ListDetail
        else -> WindowStateContentType.ListOnly
    }
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = MyCityScreen.valueOf(
        backStackEntry?.destination?.route ?: MyCityScreen.Start.name
    )
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(topBar = {
        MyCityAppBar(
            title = when (currentScreen.name) {
                MyCityScreen.CategoryList.name -> R.string.categories
                MyCityScreen.PlacesList.name -> uiState.currentCategory!!.name
                MyCityScreen.Place.name -> uiState.currentCategory!!.name
                else -> R.string.app_name
            },
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() },
        )
    }, bottomBar = {
        if (currentScreen.name != MyCityScreen.CategoryList.name && contentType != WindowStateContentType.ListDetail) {
            NavButtonsAppBar(
                nextFunction = {
                    when (currentScreen.name) {
                        MyCityScreen.Start.name -> {
                            navController.navigate(MyCityScreen.CategoryList.name)
                        }

                        MyCityScreen.PlacesList.name -> {
                            viewModel.getNextCategory()?.let { viewModel.updateCurrentCategory(it) }
                        }

                        else -> {
                            viewModel.getNextPlace()?.let { viewModel.updateCurrentPlace(it) }
                        }
                    }
                },
                previousFunction = {
                    when (currentScreen.name) {
                        MyCityScreen.PlacesList.name -> {
                            viewModel.getPreviousCategory()
                                ?.let { viewModel.updateCurrentCategory(it) }
                        }

                        MyCityScreen.Place.name -> {
                            viewModel.getPreviousPlace()?.let { viewModel.updateCurrentPlace(it) }
                        }
                    }
                },
                hasPreviousButton = when (currentScreen.name) {
                    MyCityScreen.Start.name -> false
                    MyCityScreen.CategoryList.name -> false
                    else -> true
                },
                id= when(currentScreen.name){
                    MyCityScreen.Place.name -> when (uiState.currentCategory?.name){
                        R.string.restaurants_category -> R.drawable.restaurant_icon
                        R.string.bars_category -> R.drawable.bar_icon
                        R.string.shops_category -> R.drawable.shops_icon
                        R.string.parks_category -> R.drawable.nature_icon
                        R.string.attractions_category -> R.drawable.attractions_icon
                        else-> -1
                    }
                    else -> -1
                }
            )
        }
    }) { innerPadding ->


        NavHost(navController = navController, startDestination = MyCityScreen.Start.name) {
            composable(
                route = MyCityScreen.Start.name
            ) {
                if (contentType == WindowStateContentType.ListDetail) {
                    ExpandedStartScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                } else {
                    StartScreen(modifier = Modifier.fillMaxSize())
                }
            }
            composable(
                route = MyCityScreen.CategoryList.name
            ) {
                PickCategoryScreen(
                    viewModel = viewModel,
                    navigateFunction = { navController.navigate(MyCityScreen.PlacesList.name) },
                    uiState = uiState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            composable(
                route = MyCityScreen.PlacesList.name
            ) {
                PickPlaceScreen(
                    navigateFunction = { navController.navigate(MyCityScreen.Place.name) },
                    viewModel = viewModel,
                    uiState = uiState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )

            }
            composable(
                route = MyCityScreen.Place.name
            ) {
                PlaceScreen(
                    uiState = uiState, modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCityAppBar(
    modifier: Modifier = Modifier,
    @StringRes title: Int = R.string.app_name,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
) {
    TopAppBar(colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = stringResource(id = title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                if (title == R.string.app_name) {
                    Image(
                        painter = painterResource(id = R.drawable.emblem_of_yerevan),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(
                                end = dimensionResource(id = R.dimen.padding_medium)
                            )
                    )
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button)
                    )
                }
            }
        })
}

@Composable
fun NavButtonsAppBar(
    nextFunction: () -> Unit,
    modifier: Modifier = Modifier,
    id: Int = -1,
    hasPreviousButton: Boolean,
    previousFunction: () -> Unit = {}
) {
    BottomAppBar {
        Row(horizontalArrangement = Arrangement.End, modifier = modifier.fillMaxWidth()) {
            if (hasPreviousButton) {
                NavButton(onClick = previousFunction, id = id, isNextButton = false)
                Spacer(modifier = Modifier.weight(1f))
            }

            NavButton(onClick = nextFunction, id = id, isNextButton = true)


        }
    }
}

@Composable
fun NavButton(onClick: () -> Unit, id: Int, isNextButton: Boolean) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Text(
            text = if (isNextButton) stringResource(id = R.string.next_button) else stringResource(
                id = R.string.previous_button
            ), style = MaterialTheme.typography.labelMedium
        )
        if (id != -1) Icon(
            imageVector = ImageVector.vectorResource(id = id), contentDescription = null
        )
        Icon(
            imageVector = if (isNextButton) Icons.Default.KeyboardArrowRight else Icons.Default.KeyboardArrowLeft,
            contentDescription = null
        )
    }
}

@Preview(device = Devices.TABLET)
@Composable
fun MyAppPreview() {
    MyCityTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MyCityApp(windowSize = WindowWidthSizeClass.Expanded)
        }
    }
}