package org.odk.collect.android.activities

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import org.odk.collect.android.R
import org.odk.collect.android.application.Collect
import org.odk.collect.android.injection.config.AppDependencyModule
import org.odk.collect.android.projects.ManualProjectCreatorDialog
import org.odk.collect.android.projects.QrCodeProjectCreatorDialog
import org.odk.collect.android.support.CollectHelpers
import org.odk.collect.android.version.VersionInformation
import org.odk.collect.androidtest.ActivityScenarioLauncherRule
import org.odk.collect.androidtest.RecordedIntentsRule
import org.odk.collect.strings.localization.getLocalizedString

@RunWith(AndroidJUnit4::class)
class FirstLaunchActivityTest {

    @get:Rule
    val launcherRule = ActivityScenarioLauncherRule()

    @get:Rule
    val activityRule = RecordedIntentsRule()

    @Test
    fun `MainMenuActivity should be started if there is at least one project`() {
        CollectHelpers.setupDemoProject()

        launcherRule.launch(FirstLaunchActivity::class.java)

        Intents.intended(IntentMatchers.hasComponent(MainMenuActivity::class.java.name))
    }

    @Test
    fun `The QrCodeProjectCreatorDialog should be displayed after clicking on the 'Configure with QR code' button`() {
        val scenario = launcherRule.launch(FirstLaunchActivity::class.java)
        scenario.onActivity {
            onView(withText(R.string.configure_with_qr_code)).perform(click())
            assertThat(
                it.supportFragmentManager.findFragmentByTag(QrCodeProjectCreatorDialog::class.java.name),
                `is`(notNullValue())
            )
        }
    }

    @Test
    fun `The ManualProjectCreatorDialog should be displayed after clicking on the 'Configure manually' button`() {
        val scenario = launcherRule.launch(FirstLaunchActivity::class.java)
        scenario.onActivity {
            onView(withText(R.string.configure_manually)).perform(click())
            assertThat(
                it.supportFragmentManager.findFragmentByTag(ManualProjectCreatorDialog::class.java.name),
                `is`(notNullValue())
            )
        }
    }

    @Test
    fun `The ODK logo should be displayed`() {
        val scenario = launcherRule.launch(FirstLaunchActivity::class.java)
        scenario.onActivity {
            onView(withId(R.id.logo)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `The app name with its version should be displayed`() {
        val versionInformation = mock(VersionInformation::class.java)
        whenever(versionInformation.versionToDisplay).thenReturn("vfake")
        CollectHelpers.overrideAppDependencyModule(object : AppDependencyModule() {
            override fun providesVersionInformation(): VersionInformation {
                return versionInformation
            }
        })

        val scenario = launcherRule.launch(FirstLaunchActivity::class.java)
        scenario.onActivity {
            verify(versionInformation).versionToDisplay
            onView(
                withText(
                    ApplicationProvider.getApplicationContext<Collect>().getLocalizedString(
                        R.string.collect_app_name
                    ) + " vfake"
                )
            ).check(matches(isDisplayed()))
        }
    }
}
