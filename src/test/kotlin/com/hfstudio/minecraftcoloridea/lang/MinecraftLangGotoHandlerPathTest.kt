package com.hfstudio.minecraftcoloridea.lang

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MinecraftLangGotoHandlerPathTest {
    @Test
    fun acceptsLocalizationResourceFilesOnly() {
        assertTrue(MinecraftLangGotoHandler.isLocalizationResourcePath("assets/example/lang/en_us.lang"))
        assertTrue(MinecraftLangGotoHandler.isLocalizationResourcePath("assets/example/lang/en_us.json"))
        assertFalse(MinecraftLangGotoHandler.isLocalizationResourcePath("src/main/java/example/LightDarkMode.java"))
        assertFalse(MinecraftLangGotoHandler.isLocalizationResourcePath("src/main/resources/example/data.txt"))
    }
}
