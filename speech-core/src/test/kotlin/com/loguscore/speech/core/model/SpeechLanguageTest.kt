package com.loguscore.speech.core.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SpeechLanguageTest {

    @Test
    fun `instantiates valid language tags`() {
        val lang = SpeechLanguage("en-US")
        assertEquals("en-US", lang.tag)
        assertFalse(lang.isAuto)
    }

    @Test
    fun `identifies auto detection tag correctly`() {
        val autoLang = SpeechLanguage.AUTO
        assertEquals("auto", autoLang.tag)
        assertTrue(autoLang.isAuto)

        val customAuto = SpeechLanguage("AuTo")
        assertTrue(customAuto.isAuto)
    }

    @Test
    fun `throws exception for blank or empty language tag`() {
        assertFailsWith<IllegalArgumentException> {
            SpeechLanguage("")
        }
        assertFailsWith<IllegalArgumentException> {
            SpeechLanguage("   ")
        }
    }
}
