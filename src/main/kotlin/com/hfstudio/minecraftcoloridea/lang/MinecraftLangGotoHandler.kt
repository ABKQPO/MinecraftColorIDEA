package com.hfstudio.minecraftcoloridea.lang

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import org.jetbrains.annotations.Nls

class MinecraftLangGotoHandler : GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(
        sourceElement: PsiElement?,
        offset: Int,
        editor: Editor?
    ): Array<PsiElement>? {
        if (sourceElement == null || editor == null) return null
        if (!sourceElement.isValid) return null

        val filePath = sourceElement.containingFile?.virtualFile?.path
        if (!isLocalizationResourcePath(filePath)) {
            return null
        }

        val project = sourceElement.project
        val key = locateCandidateKey(editor, offset) ?: return null
        val langFiles = FilenameIndex.getAllFilesByExt(project, "lang")
        val navigationElements = mutableListOf<PsiElement>()

        for (virtualFile in langFiles) {
            if (!virtualFile.isValid) continue

            val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: continue
            if (!psiFile.isValid) continue

            for (result in findKeyLineIndexes(psiFile, key)) {
                navigationElements.add(
                    LangKeyNavigationElement(
                        psiFile = psiFile,
                        lineIndex = result.lineIndex,
                        matchedKey = result.matchedKey,
                        matchType = result.matchType
                    )
                )
            }
        }

        navigationElements.sortBy { element ->
            if (element is LangKeyNavigationElement) element.matchType.priority else Int.MAX_VALUE
        }

        return navigationElements.takeIf { it.isNotEmpty() }?.toTypedArray()
    }

    override fun getActionText(context: DataContext): @Nls String = "Go to .lang key"

    private fun locateCandidateKey(editor: Editor, offset: Int): String? {
        val document = editor.document
        val caretOffset = offset.coerceIn(0, document.textLength)
        val lineNumber = document.getLineNumber(caretOffset)
        val lineStart = document.getLineStartOffset(lineNumber)
        val lineEnd = document.getLineEndOffset(lineNumber)
        val lineText = document.charsSequence.subSequence(lineStart, lineEnd).toString()
        val lineOffset = caretOffset - lineStart

        return com.hfstudio.minecraftcoloridea.navigation.MinecraftLangEntryKeyLocator
            .locateLang(lineText, lineOffset)
            ?.key
    }

    internal companion object {
        internal fun isLocalizationResourcePath(path: String?): Boolean {
            val normalized = path?.replace('\\', '/') ?: return false
            return normalized.contains("/lang/") &&
                (normalized.endsWith(".lang") || normalized.endsWith(".json"))
        }
    }

    private fun findKeyLineIndexes(psiFile: com.intellij.psi.PsiFile, searchKey: String): List<SearchResult> {
        if (!psiFile.isValid) return emptyList()

        val results = mutableListOf<SearchResult>()
        val lines = psiFile.text.lines()

        for ((index, line) in lines.withIndex()) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue

            val equalsIndex = trimmed.indexOf('=')
            if (equalsIndex == -1) continue

            val lineKey = trimmed.substring(0, equalsIndex).trim()

            when {
                lineKey == searchKey -> results.add(SearchResult(index, lineKey, MatchType.EXACT))
                lineKey.endsWith(".$searchKey") -> results.add(SearchResult(index, lineKey, MatchType.SUFFIX))
                lineKey.contains(searchKey) -> results.add(SearchResult(index, lineKey, MatchType.CONTAINS))
                lineKey.split('.').any { part -> part == searchKey } -> results.add(SearchResult(index, lineKey, MatchType.PARTIAL_MATCH))
            }
        }

        return results.distinctBy { it.matchedKey }.sortedBy { it.matchType.priority }
    }

    private data class SearchResult(
        val lineIndex: Int,
        val matchedKey: String,
        val matchType: MatchType
    )
}
