package com.jeripurnama.pentaword.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Honeycomb(
    centerLetter: Char,
    outerLetters: List<Char>,
    onLetterClick: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    val hexSize = 72.dp
    val hexWidth = hexSize
    val hexHeight = hexSize
    val horizontalSpacing = 4.dp
    val verticalSpacing = 4.dp

    val horizontalOffset = hexWidth * 0.75f + horizontalSpacing
    val verticalOffset = hexHeight * 0.5f + verticalSpacing

    Box(
        modifier = modifier.size(
            width = hexWidth * 2.5f + horizontalSpacing * 2,
            height = hexHeight * 3 + verticalSpacing * 2
        ),
        contentAlignment = Alignment.Center
    ) {
        // Center hexagon
        HexagonButton(
            letter = centerLetter,
            isCenter = true,
            onClick = { onLetterClick(centerLetter) },
            modifier = Modifier.align(Alignment.Center)
        )

        // Outer hexagons in honeycomb pattern
        if (outerLetters.size >= 6) {
            // Top
            HexagonButton(
                letter = outerLetters[0],
                isCenter = false,
                onClick = { onLetterClick(outerLetters[0]) },
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = -(hexHeight + verticalSpacing))
            )

            // Top-right
            HexagonButton(
                letter = outerLetters[1],
                isCenter = false,
                onClick = { onLetterClick(outerLetters[1]) },
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = horizontalOffset,
                        y = -verticalOffset
                    )
            )

            // Bottom-right
            HexagonButton(
                letter = outerLetters[2],
                isCenter = false,
                onClick = { onLetterClick(outerLetters[2]) },
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = horizontalOffset,
                        y = verticalOffset
                    )
            )

            // Bottom
            HexagonButton(
                letter = outerLetters[3],
                isCenter = false,
                onClick = { onLetterClick(outerLetters[3]) },
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = hexHeight + verticalSpacing)
            )

            // Bottom-left
            HexagonButton(
                letter = outerLetters[4],
                isCenter = false,
                onClick = { onLetterClick(outerLetters[4]) },
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = -horizontalOffset,
                        y = verticalOffset
                    )
            )

            // Top-left
            HexagonButton(
                letter = outerLetters[5],
                isCenter = false,
                onClick = { onLetterClick(outerLetters[5]) },
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = -horizontalOffset,
                        y = -verticalOffset
                    )
            )
        }
    }
}
