package com.jeripurnama.pentaword.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeripurnama.pentaword.domain.Message

@Composable
fun WordDisplay(
    currentWord: String,
    centerLetter: Char,
    message: Message?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Current word display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            if (currentWord.isNotEmpty()) {
                Text(
                    text = buildAnnotatedString {
                        currentWord.forEach { char ->
                            val isCenter = char.uppercaseChar() == centerLetter.uppercaseChar()
                            withStyle(
                                style = SpanStyle(
                                    color = if (isCenter) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp
                                )
                            ) {
                                append(char.uppercaseChar())
                            }
                        }
                    },
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Type or click",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Message display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = message != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                message?.let {
                    Text(
                        text = it.text,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (it.isError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
