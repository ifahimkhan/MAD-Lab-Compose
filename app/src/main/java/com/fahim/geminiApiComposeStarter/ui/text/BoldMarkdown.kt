package com.fahim.geminiApiComposeStarter.ui.text

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

private val BOLD_PATTERN = Regex("""\*\*(.*?)\*\*""")

/** Port of the reference app's TextFormatter: renders `**bold**` spans, drops the markers. */
fun String.toBoldAnnotatedString(): AnnotatedString = buildAnnotatedString {
    var lastIndex = 0
    for (match in BOLD_PATTERN.findAll(this@toBoldAnnotatedString)) {
        append(this@toBoldAnnotatedString.substring(lastIndex, match.range.first))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(match.groupValues[1])
        }
        lastIndex = match.range.last + 1
    }
    if (lastIndex < this@toBoldAnnotatedString.length) {
        append(this@toBoldAnnotatedString.substring(lastIndex))
    }
}
