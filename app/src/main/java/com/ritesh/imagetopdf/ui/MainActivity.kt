package com.ritesh.imagetopdf.ui

import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.ritesh.imagetopdf.R
import com.ritesh.imagetopdf.databinding.ActivityMainBinding
import com.ritesh.imagetopdf.domain.utils.Utils.APP_UPDATE_CODE
import com.ritesh.imagetopdf.domain.utils.Utils.DAYS_FOR_FLEXIBLE_UPDATE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding ?= null
    private val binding get() = _binding!!

    private lateinit var appUpdateManager: AppUpdateManager

    private val listener = InstallStateUpdatedListener { installState: InstallState ->
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            showSnackbarForUpdateComplete()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.registerListener(listener)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                && (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= DAYS_FOR_FLEXIBLE_UPDATE
            ) {
                startUpdateFlow(appUpdateInfo)
            }
        }


    }

    private fun showSnackbarForUpdateComplete() {
        Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.update_msg),
            Snackbar.LENGTH_INDEFINITE).apply {
            setAction(R.string.restart) { view: View? -> appUpdateManager.completeUpdate() }
            setActionTextColor(
                ContextCompat.getColor(applicationContext, R.color.primary))
            show()
        }
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,
                this,
                APP_UPDATE_CODE)
        } catch (e: SendIntentException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                if(appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    showSnackbarForUpdateComplete()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(listener);
        _binding = null
    }


}