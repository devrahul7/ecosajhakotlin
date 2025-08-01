package com.example.ecosajha


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ecosajha.view.MainActivity

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun test_login_ui_components_and_input_are_displayed() {
        composeRule.setContent {
            LoginScreen(
                state = LoginState(),
                onAction = {}
            )
        }

        composeRule.onNodeWithText("Employer").assertIsDisplayed()
        composeRule.onNodeWithText("Job Seeker").assertIsDisplayed()
        composeRule.onNodeWithText("Email Address").assertIsDisplayed()
        composeRule.onNodeWithText("Password").assertIsDisplayed()
        composeRule.onNodeWithText("Login").assertIsDisplayed()
    }

    @Test
    fun test_user_can_enter_credentials_and_select_job_seeker() {
        var loginAs: UserType? = null
        var email = ""
        var password = ""

        composeRule.setContent {
            LoginScreen(
                state = LoginState(),
                onAction = {
                    when (it) {
                        is LoginAction.EmailChanged -> email = it.value
                        is LoginAction.PasswordChanged -> password = it.value
                        is LoginAction.LoginAsChanged -> loginAs = it.value
                        else -> {}
                    }
                }
            )
        }

        // Enter text into email and password
        composeRule.onNodeWithText("Email Address").performTextInput("me@gmail.com")
        composeRule.onNodeWithText("Password").performTextInput("123456")

        // Select Job Seeker radio
        composeRule.onNodeWithText("Job Seeker").performClick()

        // Assert state changes
        composeRule.runOnIdle {
            assertEquals("me@gmail.com", email)
            assertEquals("123456", password)
            assertEquals(UserType.JOB_SEEKER, loginAs)
        }
    }
}