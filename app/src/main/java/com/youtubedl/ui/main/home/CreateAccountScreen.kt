eateAccountScreen.kt
package com.grindrapp.android.screens

import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import androidx.test.espresso.AppNotIdleException
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.web.assertion.WebViewAssertions.webContent
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.matcher.DomMatchers.containingTextInBody
import androidx.test.espresso.web.model.Atoms
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.grindrapp.android.R
import com.grindrapp.android.TestConstants
import com.grindrapp.android.utils.matchers.CustomMatchers
import com.grindrapp.android.utils.matchers.CustomMatchers.retry
import com.grindrapp.android.utils.GrindrDevice
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matchers.not
import org.junit.Assert
import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import java.util.concurrent.TimeUnit

class CreateAccountScreen : BaseScreen() {
    // check marketing by default not checked for all country
    // check create account button not enabled
    fun isLoad(): ViewInteraction {
        waitProgressBarDisappear(5)
        waitToAppear(R.id.fragment_auth_email)
        waitToAppear(R.id.fragment_auth_password)
        waitToAppear(R.id.fragment_create_account_confirm_password)
        waitToAppear(R.id.date_of_birth_input_layout)
        waitToAppear(R.id.create_account_marketing)
        onView(withId(R.id.create_account_marketing)).check(matches(isNotChecked())) // check marketing by default not checked for all country
        onView(withId(R.id.fragment_create_account_create_account_button))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled()))) // check create account button not enabled
        return onView(withId(R.id.fragment_create_account_create_account_button))
    }

    fun typeEmail(email: String?) {
        clickEmailField()
        onView(withId(R.id.fragment_auth_email)).perform(
            clearText(),
            replaceText(email),
            closeSoftKeyboard(),
        )
    }

    fun typePassword(password: String?) {
        clickPasswordField()
        onView(withId(R.id.fragment_auth_password)).perform(clearText(), replaceText(password))
    }

    fun typeConfirmPassword(password: String?) {
        clickConfirmPasswordField()
        onView(withId(R.id.fragment_create_account_confirm_password)).perform(
            clearText(),
            replaceText(password),
        )
    }

    fun clickBirth() {
        onView(withId(R.id.date_of_birth_input_layout)).perform(click())
    }

    fun setDateOfBirth(year: Int, month: Int, day: Int) {
        val ageGatingScreen = AgeGatingScreen()
        ageGatingScreen.verifyDisplayed()
        ageGatingScreen.typeBirthDay("%02d".format(month), "%02d".format(day), "%04d".format(year))
        ageGatingScreen.clickNextButton()
        checkBirthDayText(year, month, day)
    }

    fun checkEmailInputError(error: String?): ViewInteraction {
        return onView(withId(R.id.email_input_layout)).check(
            matches(
                CustomMatchers.hasErrorText(
                    error,
                ),
            ),
        )
    }

    fun checkPasswordInputError(error: String?): ViewInteraction {
        return onView(withId(R.id.password_input_layout)).check(
            matches(
                CustomMatchers.hasErrorText(
                    error,
                ),
            ),
        )
    }

    fun checkConfirmPasswordInputError(error: String?): ViewInteraction {
        return onView(withId(R.id.confirm_password_input_layout)).check(
            matches(
                CustomMatchers.hasErrorText(
                    error,
                ),
            ),
        )
    }

    fun checkBirthdayInputError(error: String?): ViewInteraction {
        return onView(withId(R.id.date_of_birth_input_layout)).check(
            matches(
                CustomMatchers.hasErrorText(
                    error,
                ),
            ),
        )
    }

    fun checkBirthDayText(year: Int, month: Int, day: Int): ViewInteraction {
        val DOB = GregorianCalendar(year, month - 1, day).time // calendar month start with index 0
        return onView(withId(R.id.fragment_create_account_date_of_birth)).check(
            matches(
                withText(
                    SimpleDateFormat("MMM d, yyyy").format(DOB).toString(),
                ),
            ),
        )
    }

    fun checkCreateAccountButtonEnabled(): ViewInteraction {
        return onView(withId(R.id.fragment_create_account_create_account_button)).check(
            matches(
                isEnabled(),
            ),
        )
    }

    fun checkCreateAccountMarketingEmailCheckBoxChecked() {
        onView(withId(R.id.create_account_marketing)).check(matches(isChecked()))
    }

    fun clickEmailField() {
        onView(withId(R.id.fragment_auth_email)).perform(click())
    }

    fun clickPasswordField() {
        onView(withId(R.id.fragment_auth_password)).perform(click())
    }

    fun clickConfirmPasswordField() {
        onView(withId(R.id.fragment_create_account_confirm_password)).perform(click())
    }

    fun clickCreateAccount() {
        onView(withId(R.id.fragment_create_account_create_account_button)).perform(click())
    }

    fun clickCreateAccountMarketingEmailCheckBox() {
        onView(withId(R.id.create_account_marketing)).perform(click())
    }

    // Check captcha
    fun checkCaptchaDisplay() {
        waitToAppear(R.id.captcha_title, 10)
    }

    // Accept Terms of Service
    fun acceptTermsOfService() {
        waitToAppearInRootNotPopup(
            matcher = withId(R.id.proceed_button),
            assertion = matches(withEffectiveVisibility(Visibility.VISIBLE)),
        )
        onView(withId(R.id.proceed_button)).perform(click())
        clickOk()
    }

    // Accept Privacy Policy
    fun acceptPrivacyPolicy() {
        retry {
            onView(withId(R.id.proceed_button))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .perform(click())
        }
        clickOk()
    }

    fun loginWith3rdPartyGoogle() {
        IdlingPolicies.setMasterPolicyTimeout(10, TimeUnit.SECONDS)
        IdlingPolicies.setIdlingResourceTimeout(10, TimeUnit.SECONDS)
        try {
            onView(withId(R.id.google_login_button))
                .check(matches(isDisplayed()))
                .perform(click())
        } catch (ex: AppNotIdleException) {
            backgroundThenForegroundApp()
            onView(withId(R.id.google_login_button))
                .check(matches(isDisplayed()))
                .perform(click())
        }
    }

    fun loginWith3rdPartyFacebook() {
        IdlingPolicies.setMasterPolicyTimeout(45, TimeUnit.SECONDS)
        IdlingPolicies.setIdlingResourceTimeout(45, TimeUnit.SECONDS)
        try {
            if (!GrindrDevice.isEmulator) {
                onView(withId(R.id.facebook_login_button))
                    .check(matches(isDisplayed()))
                    .perform(click())
            } else {
                if (uiDevice.wait(
                        Until.hasObject(
                            By.res(
                                TestConstants.APP_PACKAGE,
                                "facebook_login_button",
                            ),
                        ), 3000,
                    )
                ) {
                    uiDevice.findObject(By.res(TestConstants.APP_PACKAGE, "facebook_login_button"))
                        .click()
                }
            }
        } catch (ex: AppNotIdleException) {
            backgroundThenForegroundApp()
            if (!GrindrDevice.isEmulator) {
                onView(withId(R.id.facebook_login_button))
                    .check(matches(isDisplayed()))
                    .perform(click())
            } else {
                if (uiDevice.wait(
                        Until.hasObject(
                            By.res(
                                TestConstants.APP_PACKAGE,
                                "facebook_login_button",
                            ),
                        ), 3000,
                    )
                ) {
                    uiDevice.findObject(By.res(TestConstants.APP_PACKAGE, "facebook_login_button"))
                        .click()
                }
            }
        }
        if (uiDevice.wait(Until.hasObject(By.text("Terms and Privacy Policy")), 3000)) {
            uiDevice.findObject(By.text("CONTINUE")).click()
        }

        // Facebook version: 250.0.0.26.241 in Samsung device
        if (uiDevice.wait(
                Until.hasObject(By.res(TestConstants.APP_PACKAGE, "toolbar_avatar")),
                6000,
            )
        ) { // if facebook already signed in, go to cascade directly
            // Do nothing
        } else {
            if (!GrindrDevice.isEmulator) {
                if (uiDevice.wait(
                        Until.hasObject(By.text("Automation Grindr")),
                        6000,
                    )
                ) { // if facebook not signed in, but there is an account to choose
                    uiDevice.findObject(By.text("Automation Grindr")).click()
                    if (uiDevice.wait(
                            Until.hasObject(By.text("LOG IN")),
                            3000,
                        )
                    ) { // better check, in case someone remove password. tho default FB will save it.
                        uiDevice.findObject(By.text("Password")).text = "0FeetAway"
                        uiDevice.findObject(By.text("LOG IN")).click()
                    }
                } else if (uiDevice.wait(
                        Until.hasObject(By.text("Continue")),
                        1000,
                    )
                ) { // already logged in
                    uiDevice.findObject(By.text("Continue")).click()
                } else { // if facebook not signed in, and no account to choose
                    if (uiDevice.wait(
                            Until.hasObject(By.desc("Log In")),
                            5000,
                        )
                    ) { // first time login, need to check
                        uiDevice.findObject(By.desc("Log In")).click()
                    }
                    if (uiDevice.wait(
                            Until.hasObject(By.text("Phone or email")),
                            2000,
                        )
                    ) { // signed in and sign out before.
                        uiDevice.findObject(By.text("Phone or email")).text =
                            "automationgrindr@gmail.com"
                        uiDevice.findObject(By.text("Password")).text = "0FeetAway"
                        if (uiDevice.wait(Until.hasObject(By.desc("Log In")), 10000)) {
                            uiDevice.findObject(By.desc("Log In")).click()
                        } else {
                            Assert.fail("Unable to login into Facebook account on device: [" + GrindrDevice.rawModel + "]")
                        }
                    }
                }
            } else {
                try { // webView Facebook login
                    uiDevice.wait(Until.findObject(By.clazz(WebView::class.java)), 5000)

                    // Set Login
                    val emailInput = uiDevice.findObject(
                        UiSelector()
                            .instance(0)
                            .className(EditText::class.java),
                    )
                    emailInput.waitForExists(5000)
                    emailInput.text = "automationgrindr@gmail.com"

                    // Set Password
                    val passwordInput = uiDevice.findObject(
                        UiSelector()
                            .instance(1)
                            .className(EditText::class.java),
                    )
                    passwordInput.waitForExists(5000)
                    passwordInput.text = "0FeetAway"

                    // Confirm Button Click
                    val buttonLogin = uiDevice.findObject(
                        UiSelector()
                            .instance(0)
                            .className(Button::class.java),
                    )
                    buttonLogin.waitForExists(5000)
                    buttonLogin.clickAndWaitForNewWindow()

                    // Confirm Continue Click
                    val buttonContinue = uiDevice.findObject(
                        UiSelector()
                            .instance(0)
                            .className(Button::class.java),
                    )
                    buttonContinue.waitForExists(5000)
                    buttonContinue.clickAndWaitForNewWindow()
                } catch (e: UiObjectNotFoundException) {
                    Assert.fail("Unable to login into Facebook account on device: [" + GrindrDevice.rawModel + "]")
                }
            }
        }
    }

    fun clickCAPTCHAButton() {
        waitProgressBarDisappear(40) // sometimes CAPTCHA really need long time to load.
        waitToAppear(R.id.webview_captcha, 40)
        onView(withId(R.id.webview_captcha)).perform(click())
    }

    fun bypassCaptchaIfDisplayed() {
        try {
            checkCaptchaDisplay()
            // Try click on success button directly
            waitToPerform(matcher = withId(R.id.success_button), action = click())
        } catch (e: Throwable) {
        }
    }

    fun createUserAccount(
        email: String?,
        birthYear: Int,
        birthMonth: Int,
        birthDay: Int,
    ): EditProfilePhotoAndFieldsScreen {
        setDateOfBirth(birthYear, birthMonth, birthDay)
        isLoad()
        typeEmail(email)
        typePassword("12345678")
        typeConfirmPassword("12345678")
        hideKeyboard()
        clickCreateAccount()
        bypassCaptchaIfDisplayed()
        return EditProfilePhotoAndFieldsScreen()
    }

    fun createUserAccountWithSift(
        email: String?,
        password: String?,
        birthYear: Int,
        birthMonth: Int,
        birthDay: Int,
    ): AccountVerifyScreen {
        setDateOfBirth(birthYear, birthMonth, birthDay)
        isLoad()
        typeEmail(email)
        typePassword(password)
        typeConfirmPassword(password)
        hideKeyboard()
        clickCreateAccount()
        bypassCaptchaIfDisplayed()
        return AccountVerifyScreen()
    }

    fun checkRegistrationErrorShow() {
        onView(withId(R.id.snackbar_text)).check(matches(isDisplayed()))
            .check(matches(withText("Registration Error")))
    }

    fun checkEmailExistingErrorShow() {
        onView(withId(R.id.snackbar_text)).check(matches(isDisplayed()))
            .check(matches(withText("Forget something? An account already exists for this email address.")))
    }

    fun isTOSDisplayed(isDisplayed: Boolean) {
        onWebView(withId(R.id.webview)).forceJavascriptEnabled()
        if (isDisplayed) {
            waitToAppearInRootNotPopup(
                matcher = withId(R.id.proceed_button),
                assertion = matches(withEffectiveVisibility(Visibility.VISIBLE)),
            )
            onWebView(withId(R.id.webview))
                .check(webContent(containingTextInBody("GRINDR TERMS AND CONDITIONS OF SERVICE")))
        } else {
            onView(withId(R.id.proceed_button))
                .check(doesNotExist())
            onWebView(withId(R.id.webview))
                .check(webContent(not(containingTextInBody("GRINDR TERMS AND CONDITIONS OF SERVICE"))))
        }
    }

    fun isPrivacyPolicyDisplayed(isDisplayed: Boolean) {
        onWebView(withId(R.id.webview)).forceJavascriptEnabled()
        if (isDisplayed) {
            waitToAppearInRootNotPopup(
                matcher = withId(R.id.proceed_button),
                assertion = matches(withEffectiveVisibility(Visibility.VISIBLE)),
            )
            onWebView(withId(R.id.webview)) // use reset when loading new pages
                .reset()
                .check(webMatches(Atoms.getCurrentUrl(), containsString("grindr.com")))
            onWebView(withId(R.id.webview))
                .check(webContent(containingTextInBody("Grindr's Privacy and Cookie Policy")))
        } else {
            onView(withId(R.id.proceed_button))
                .check(doesNotExist())
            onWebView(withId(R.id.webview))
                .check(webContent(not(containingTextInBody("Grindr's Privacy and Cookie Policy"))))
        }
    }

    companion object {
        private const val TAG = "CreateAccountScreen"
    }
}
