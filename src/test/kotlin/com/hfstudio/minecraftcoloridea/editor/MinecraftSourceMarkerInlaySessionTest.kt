package com.hfstudio.minecraftcoloridea.editor

import java.awt.Dimension
import java.awt.Rectangle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MinecraftSourceMarkerInlaySessionTest {
    @Test
    fun hexColorPickerPopupMinSizeUsesExpandedMinimumWidth() {
        val size = MinecraftSourceMarkerInlaySession.hexColorPickerPopupMinSize(Dimension(320, 260))

        assertEquals(MinecraftSourceMarkerInlaySession.HEX_COLOR_PICKER_MIN_WIDTH, size.width)
        assertEquals(260, size.height)
    }

    @Test
    fun hexColorPickerPopupMinSizeKeepsLargerExistingWidth() {
        val size = MinecraftSourceMarkerInlaySession.hexColorPickerPopupMinSize(Dimension(520, 260))

        assertEquals(520, size.width)
        assertEquals(260, size.height)
    }

    @Test
    fun visibleLineCaptureIsDisabledDuringBulkDocumentUpdates() {
        assertFalse(
            MinecraftColorEditorSession.canCaptureVisibleLineRange(
                isDispatchThread = true,
                isDisposed = false,
                isEditorDisposed = false,
                isDocumentInBulkUpdate = true
            )
        )
    }

    @Test
    fun visibleLineCaptureRemainsEnabledForNormalEditorState() {
        assertTrue(
            MinecraftColorEditorSession.canCaptureVisibleLineRange(
                isDispatchThread = true,
                isDisposed = false,
                isEditorDisposed = false,
                isDocumentInBulkUpdate = false
            )
        )
    }

    @Test
    fun visibleLineRangeLookupSkipsVisibleAreaAccessDuringBulkUpdates() {
        var visibleAreaRequested = false
        var computed = false

        val result = MinecraftColorEditorSession.readVisibleLineRangeIfAllowed(
            isDispatchThread = true,
            isDisposed = false,
            isEditorDisposed = false,
            isDocumentInBulkUpdate = true,
            visibleAreaSupplier = {
                visibleAreaRequested = true
                Rectangle(0, 0, 16, 16)
            },
            lineRangeComputer = {
                computed = true
                MinecraftVisibleLineRange(0, 0)
            }
        )

        assertEquals(null, result)
        assertFalse(visibleAreaRequested)
        assertFalse(computed)
    }

    @Test
    fun visibleLineRangeLookupSkipsVisibleAreaAccessOffEdt() {
        var visibleAreaRequested = false
        var computed = false

        val result = MinecraftColorEditorSession.readVisibleLineRangeIfAllowed(
            isDispatchThread = false,
            isDisposed = false,
            isEditorDisposed = false,
            isDocumentInBulkUpdate = false,
            visibleAreaSupplier = {
                visibleAreaRequested = true
                Rectangle(0, 0, 16, 16)
            },
            lineRangeComputer = {
                computed = true
                MinecraftVisibleLineRange(0, 0)
            }
        )

        assertEquals(null, result)
        assertFalse(visibleAreaRequested)
        assertFalse(computed)
    }
}
