package org.odk.collect.android.activities

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.odk.collect.analytics.Analytics
import org.odk.collect.android.R
import org.odk.collect.android.analytics.AnalyticsEvents
import org.odk.collect.android.configure.qr.AppConfigurationGenerator
import org.odk.collect.android.databinding.FirstLaunchLayoutBinding
import org.odk.collect.android.injection.DaggerUtils
import org.odk.collect.android.projects.*
import org.odk.collect.android.version.VersionInformation
import org.odk.collect.androidshared.ui.DialogFragmentUtils
import org.odk.collect.androidshared.ui.GroupClickListener.addOnClickListener
import org.odk.collect.androidshared.ui.ToastUtils
import org.odk.collect.projects.Project
import org.odk.collect.projects.ProjectsRepository
import org.odk.collect.shared.strings.Validator
import javax.inject.Inject

class FirstLaunchActivity : CollectAbstractActivity() {

    @Inject
    lateinit var projectsRepository: ProjectsRepository

    @Inject
    lateinit var projectCreator: ProjectCreator

    @Inject
    lateinit var versionInformation: VersionInformation

    @Inject
    lateinit var currentProjectProvider: CurrentProjectProvider

    @Inject
    lateinit var appConfigurationGenerator: AppConfigurationGenerator

    private fun addDsscProject() {
        val settingsJson = appConfigurationGenerator.getAppConfigurationAsJsonWithServerDetails(
                "http://kc.kobo.dev.rintio.com",
                "molympio",
                "m@l@n@@2020$"
        )
        projectCreator.createNewProject(settingsJson)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        DaggerUtils.getComponent(this).inject(this)

        if (projectsRepository.getAll().isNotEmpty()) {
            ActivityUtils.startActivityAndCloseAllOthers(this, LandingPageActivity::class.java)
            return
        }

        FirstLaunchLayoutBinding.inflate(layoutInflater).apply {
            setContentView(this.root)

            appName.text = String.format(
                "%s %s",
                getString(R.string.collect_app_name),
                versionInformation.versionToDisplay
            )

            configureLater.addOnClickListener {
                Analytics.log(AnalyticsEvents.TRY_DEMO)

                projectsRepository.save(Project.DEMO_PROJECT)
                currentProjectProvider.setCurrentProject(Project.DEMO_PROJECT_ID)

                ActivityUtils.startActivityAndCloseAllOthers(this@FirstLaunchActivity, LandingPageActivity::class.java)
            }
        }
    }
}
