package com.adimovska.runique

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.adimovska.auth.presentation.intro.IntroScreenRoot
import com.adimovska.auth.presentation.login.LoginScreenRoot
import com.adimovska.auth.presentation.register.RegisterScreenRoot
import com.adimovska.run.presentation.active_run.ActiveRunScreenRoot
import com.adimovska.run.presentation.active_run.service.ActiveRunService
import com.adimovska.run.presentation.run_overview.RunOverviewScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isLoggedIn: Boolean,
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.Run else Routes.Auth
    ) {
        authGraph(navController)
        runGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation<Routes.Auth>(
        startDestination = Routes.Intro,
    ) {
        composable<Routes.Intro> {
            IntroScreenRoot(
                onSignUpClick = {
                    navController.navigate(Routes.Register)
                },
                onSignInClick = {
                    navController.navigate(Routes.Login)
                }
            )
        }
        composable<Routes.Register> {
            RegisterScreenRoot(
                onSignInClick = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Register) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulRegistration = {
                    navController.navigate(Routes.Login)
                }
            )
        }

        composable<Routes.Login> {
            LoginScreenRoot(
                onLoginSuccess = {
                    navController.navigate(Routes.Run)
                },
                onSignUpClick = {
                    navController.navigate(Routes.Register) {
                        popUpTo(Routes.Login) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.runGraph(
    navController: NavHostController,
) {
    navigation<Routes.Run>(
        startDestination = Routes.RunOverview,
    ) {
        composable<Routes.RunOverview> {
            RunOverviewScreenRoot(
                onStartRunClick = {
                    navController.navigate(Routes.ActiveRun)
                },
                onLogoutClick = {
                    navController.navigate(Routes.Auth) {
                        popUpTo(Routes.Run) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<Routes.ActiveRun>(
            deepLinks = listOf(
                navDeepLink<Routes.ActiveRun>(
                    basePath = "runique://active_run"
                )
            )
        ) {
            val context = LocalContext.current
            ActiveRunScreenRoot(
                onBack = {
                    navController.navigateUp()
                },
                onServiceToggle = { shouldServiceRun ->
                    if (shouldServiceRun) {
                        context.startService(
                            ActiveRunService.createStartIntent(
                                context = context,
                                activityClass = MainActivity::class.java
                            )
                        )
                    } else {
                        context.startService(
                            ActiveRunService.createStopIntent(context = context)
                        )
                    }
                },
                onFinish = {
                    navController.navigateUp()
                }
            )
        }
    }
}


