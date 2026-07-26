package com.kracubo.app.ui.screens.codeeditor.editor.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle

private val kotlinKeywords = listOf(
    "package", "import", "class", "fun", "val", "var",
    "if", "else", "for", "while", "return",
    "override", "object", "interface", "private",
    "protected", "public", "internal", "when",
    "try", "catch", "finally", "throw", "null",
    "true", "false", "this", "super", "as", "is",
    "in", "out", "by", "companion", "data", "enum",
    "sealed", "abstract", "open", "final", "const",
    "lateinit", "suspend", "async", "await", "with",
    "let", "run", "apply", "also", "repeat", "break",
    "continue", "do", "when", "typealias"
)

fun highlightKotlin(code: String): AnnotatedString {
    val builder = AnnotatedString.Builder(code)

    fun highlight(regex: Regex, color: Color) {
        regex.findAll(code).forEach { matchResult ->
            builder.addStyle(
                SpanStyle(color = color),
                matchResult.range.first,
                matchResult.range.last + 1
            )
        }
    }

    highlight(Regex("//.*"), CodeColors.Comment)

    highlight(Regex("/\\*[\\s\\S]*?\\*/"), CodeColors.Comment)

    highlight(Regex("\"(\\\\.|[^\"])*\""), CodeColors.String)
    highlight(Regex("'(\\\\.|[^'])*'"), CodeColors.String)
    highlight(Regex("`[^`]*`"), CodeColors.String)

    highlight(Regex("\\b\\d+(\\.\\d+)?[fFdDlL]?\\b"), CodeColors.Number)
    highlight(Regex("\\b0x[0-9a-fA-F]+\\b"), CodeColors.Number)

    kotlinKeywords.forEach { keyword ->
        highlight(Regex("\\b$keyword\\b"), CodeColors.Keyword)
    }

    highlight(Regex("\\b[A-Z][A-Za-z0-9_]*\\b"), CodeColors.Type)

    return builder.toAnnotatedString()
}

