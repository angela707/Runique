package com.adimovska.runique

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.adimovska.core.presentation.designsystem.RuniqueTheme
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModel<MainViewModel>()


    private lateinit var splitInstallManager: SplitInstallManager
    private val splitInstallListener = SplitInstallStateUpdatedListener { state ->
        // listen to the installation state
        when (state.status()) {
            SplitInstallSessionStatus.INSTALLED -> {
                viewModel.setAnalyticsDialogVisibility(false)
                Toast.makeText(
                    applicationContext,
                    R.string.analytics_installed,
                    Toast.LENGTH_LONG
                ).show()
            }

            SplitInstallSessionStatus.INSTALLING -> {
                viewModel.setAnalyticsDialogVisibility(true)
            }

            SplitInstallSessionStatus.DOWNLOADING -> {
                viewModel.setAnalyticsDialogVisibility(true)
            }

            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                splitInstallManager.startConfirmationDialogForResult(state, this, 0)
            }

            SplitInstallSessionStatus.FAILED -> {
                viewModel.setAnalyticsDialogVisibility(false)
                Toast.makeText(
                    applicationContext,
                    R.string.error_installation_failed,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.state.value.isCheckingAuth
            }
        }
        enableEdgeToEdge()
        splitInstallManager = SplitInstallManagerFactory.create(applicationContext)
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            RuniqueTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (!state.isCheckingAuth) {
                        val navController = rememberNavController()
                        NavigationRoot(
                            navController = navController,
                            isLoggedIn = state.isLoggedIn,
                            onAnalyticsClick = {
                                installOrStartAnalyticsFeature()
                            }
                        )
                    }
                    if (state.showAnalyticsInstallDialog) {
                        AnalyticsDialog()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        splitInstallManager.registerListener(splitInstallListener)
    }

    override fun onPause() {
        super.onPause()
        splitInstallManager.unregisterListener(splitInstallListener)
    }

    private fun installOrStartAnalyticsFeature() {
        if (splitInstallManager.installedModules.contains("analytics_feature")) {
            //if it is installed launch it
            Intent()
                .setClassName(
                    packageName,
                    "com.adimovska.analytics.analytics_feature.AnalyticsActivity"
                )
                .also(::startActivity)
            return
        }

        //if not install it

        val request = SplitInstallRequest.newBuilder()
            .addModule("analytics_feature")
            .build()
        splitInstallManager
            .startInstall(request)
            .addOnFailureListener {
                it.printStackTrace()
                Toast.makeText(
                    applicationContext,
                    R.string.error_couldnt_load_module,
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}

@Composable
private fun AnalyticsDialog(
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = {}) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.installing_module),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}