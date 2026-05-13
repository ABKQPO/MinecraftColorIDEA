package com.hfstudio.minecraftcoloridea.settings

import com.hfstudio.minecraftcoloridea.core.MinecraftColorConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MinecraftColorSettingsStateTest {
    @Test
    fun persistsConfiguredGlobalInferenceLimit() {
        val state = MinecraftColorSettingsState()

        state.update(MinecraftColorConfig(maxEnumeratedKeys = 12))

        assertEquals(12, state.toConfig().maxEnumeratedKeys)
    }

    @Test
    fun clampsInvalidPersistedGlobalInferenceLimit() {
        val state = MinecraftColorSettingsState()
        val storedState = MinecraftColorSettingsState.StoredState().apply {
            maxEnumeratedKeys = 200
        }

        state.loadState(storedState)

        assertEquals(MinecraftColorConfig.DEFAULT_MAX_ENUMERATED_KEYS, state.toConfig().maxEnumeratedKeys)
    }

    @Test
    fun persistsLangColorOverrides() {
        val state = MinecraftColorSettingsState()

        state.langKeyColor = 0x112233
        state.langEqualColor = 0x445566

        assertEquals(0x112233, state.langKeyColor)
        assertEquals(0x445566, state.langEqualColor)
        assertNotEquals(state.langKeyColor, state.langEqualColor)
    }
}
