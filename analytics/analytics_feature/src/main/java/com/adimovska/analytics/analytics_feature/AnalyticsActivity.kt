package com.adimovska.analytics.analytics_feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.adimovska.analytics.di.analyticsModule
import com.adimovska.analytics.presentation.AnalyticsDashboardScreenRoot
import com.adimovska.analytics.presentation.di.analyticsPresentationModule
import com.adimovska.core.presentation.designsystem.RuniqueTheme
import com.google.android.play.core.splitcompat.SplitCompat
import org.koin.core.context.loadKoinModules

class AnalyticsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(
            listOf(
                analyticsModule,
                analyticsPresentationModule
            )
        )
        SplitCompat.installActivity(this)

        setContent {
            RuniqueTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = AnalyticsRoutes.Analytics
                ) {
                    analyticsGraph(navController = navController, onActivityFinish = { finish() })
                }
            }
        }
    }
}


private fun NavGraphBuilder.analyticsGraph(
    navController: NavHostController,
    onActivityFinish: () -> Unit
) {
    navigation<AnalyticsRoutes.Analytics>(
        startDestination = AnalyticsRoutes.AnalyticsOverview,
    ) {
        composable<AnalyticsRoutes.AnalyticsOverview> {
            AnalyticsDashboardScreenRoot(
                onBackClick = onActivityFinish
            )
        }
    }
}