package com.hfstudio.minecraftcoloridea.lang

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class MinecraftLangCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .inFile(
                    PlatformPatterns.psiFile()
                        .withName(PlatformPatterns.string().endsWith(".lang"))
                ),
            object : CompletionProvider<com.intellij.codeInsight.completion.CompletionParameters>() {
                override fun addCompletions(
                    parameters: com.intellij.codeInsight.completion.CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val offset = parameters.editor.caretModel.offset
                    val document = parameters.editor.document
                    val lineNumber = document.getLineNumber(offset)
                    val lineStart = document.getLineStartOffset(lineNumber)
                    val textBeforeCursorInLine = document.getText(TextRange(lineStart, offset))

                    if (!textBeforeCursorInLine.contains("=")) {
                        return
                    }

                    ALL_CODES.forEach { (name, code) ->
                        result.addElement(
                            LookupElementBuilder.create(name)
                                .withInsertHandler { ctx, _ ->
                                    val editor = ctx.editor
                                    val doc = editor.document
                                    doc.replaceString(ctx.startOffset, ctx.tailOffset, code)
                                    editor.caretModel.moveToOffset(ctx.startOffset + code.length)
                                }
                                .withTypeText(code)
                        )
                    }
                }
            }
        )
    }
}

val ALL_CODES = MinecraftFormatting.getAllForAutoComplete()
