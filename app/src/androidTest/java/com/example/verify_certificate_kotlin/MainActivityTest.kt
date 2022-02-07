package com.example.verify_certificate_kotlin

import androidx.test.core.app.ActivityScenario
import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Test


@RunWith(AndroidJUnit4::class)
@LargeTest

class MainActivityTest {
    @get: Rule
    var rule: ActivityScenarioRule<*> = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    @Test
    fun testEmptyDomainIsInvalid() {
        onView(withId(R.id.editTextUrl))
            .perform(typeText(""), closeSoftKeyboard())

        onView(withId(R.id.buttonVerify)).perform(click())

        onView(withId(R.id.textViewResult)).check(matches(withText(INVALID_DOMAIN)))
    }

    @Test
    fun testSingleCharDomainIsInvalid() {
        onView(withId(R.id.editTextUrl))
            .perform(typeText("x"), closeSoftKeyboard())

        onView(withId(R.id.buttonVerify)).perform(click())

        onView(withId(R.id.textViewResult)).check(matches(withText(INVALID_DOMAIN)))
    }

    @Test
    fun testValidDomainDotCom() {
        onView(withId(R.id.editTextUrl))
            .perform(typeText("google.com"), closeSoftKeyboard())

        onView(withId(R.id.buttonVerify)).perform(click())

        onView(withId(R.id.textViewResult)).check(matches(withText(VALID)))
    }

    @Test
    fun testValidDomainWwwDotCom() {
        onView(withId(R.id.editTextUrl))
            .perform(typeText("www.google.com"), closeSoftKeyboard())

        onView(withId(R.id.buttonVerify)).perform(click())

        onView(withId(R.id.textViewResult)).check(matches(withText(VALID)))
    }

    @Test
    fun testValidDomainDotOrg() {
        onView(withId(R.id.editTextUrl))
            .perform(typeText("wikipedia.org"), closeSoftKeyboard())

        onView(withId(R.id.buttonVerify)).perform(click())

        onView(withId(R.id.textViewResult)).check(matches(withText(VALID)))
    }

    @Test
    fun testValidDomainDotEdu() {
        onView(withId(R.id.editTextUrl))
            .perform(typeText("uci.edu"), closeSoftKeyboard())

        onView(withId(R.id.buttonVerify)).perform(click())

        onView(withId(R.id.textViewResult)).check(matches(withText(VALID)))
    }


    @Test
    fun testInvalidExpired() {
        onView(withId(R.id.editTextUrl))
            .perform(typeText("expired.badssl.com"), closeSoftKeyboard())

        onView(withId(R.id.buttonVerify)).perform(click())

        onView(withId(R.id.textViewResult)).check(matches(withText(INVALID)))
    }

    @Test
    fun testInvalidWrongHost() {
        onView(withId(R.id.editTextUrl))
            .perform(typeText("wrong.host.badssl.com"), closeSoftKeyboard())

        onView(withId(R.id.buttonVerify)).perform(click())

        onView(withId(R.id.textViewResult)).check(matches(withText(INVALID_DOMAIN)))
    }

    @Test
    fun testInvalidSelfSigned() {
        onView(withId(R.id.editTextUrl))
            .perform(typeText("self-signed.badssl.com"), closeSoftKeyboard())

        onView(withId(R.id.buttonVerify)).perform(click())

        onView(withId(R.id.textViewResult)).check(matches(withText(INVALID)))
    }

    @Test
    fun testInvalidUntrustedRoot() {
        onView(withId(R.id.editTextUrl))
            .perform(typeText("untrusted-root.badssl.com"), closeSoftKeyboard())

        onView(withId(R.id.buttonVerify)).perform(click())

        onView(withId(R.id.textViewResult)).check(matches(withText(INVALID)))
    }

    @Test
    fun testInvalidRevoked() {
        onView(withId(R.id.editTextUrl))
            .perform(typeText("revoked.badssl.com"), closeSoftKeyboard())

        onView(withId(R.id.buttonVerify)).perform(click())

        onView(withId(R.id.textViewResult)).check(matches(withText(INVALID)))
    }

    @Test
    fun testInvalidPinning() {
        onView(withId(R.id.editTextUrl))
            .perform(typeText("pinning-test.badssl.com"), closeSoftKeyboard())

        onView(withId(R.id.buttonVerify)).perform(click())

        onView(withId(R.id.textViewResult)).check(matches(withText(INVALID)))
    }

    companion object {
        val INVALID_DOMAIN = "Invalid Domain"
        val INVALID = "Invalid"
        val VALID = "Valid"
    }

}