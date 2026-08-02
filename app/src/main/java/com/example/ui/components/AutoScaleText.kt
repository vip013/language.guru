package com.example.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun AutoScaleText(
    text: String,
    initialFontSize: TextUnit,
    color: Color,
    fontWeight: FontWeight,
    textAlign: TextAlign,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    minFontSize: TextUnit = 9.sp
) {
    var fontSize by remember(text, initialFontSize) { mutableStateOf(initialFontSize) }
    var readyToDraw by remember(text, initialFontSize) { mutableStateOf(false) }

    Text(
        text = text,
        fontSize = fontSize,
        lineHeight = (fontSize.value * 1.15f).sp,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        softWrap = maxLines > 1,
        overflow = TextOverflow.Clip,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow && fontSize.value > minFontSize.value) {
                fontSize = (fontSize.value - 1f).sp
            } else {
                readyToDraw = true
            }
        },
        modifier = modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        }
    )
}
