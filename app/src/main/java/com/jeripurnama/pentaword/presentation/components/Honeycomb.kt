package com.jeripurnama.pentaword.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun Honeycomb(
    centerLetter: Char,
    outerLetters: List<Char>,
    onLetterClick: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    val hexSize = 80.dp
    val hexWidth = hexSize
    val hexHeight = hexSize
    val horizontalSpacing = 12.dp
    val verticalSpacing = 10.dp

    // Slight random offsets for organic feel (seeded by letters for consistency)
    val randomOffsets = remember(centerLetter, outerLetters) {
        val seed = centerLetter.code + outerLetters.sumOf { it.code }
        val random = Random(seed)
        List(6) {
            Pair(
                (random.nextFloat() * 6 - 3).dp,
                (random.nextFloat() * 6 - 3).dp
            )
        }
    }

    val horizontalOffset = hexWidth * 0.78f + horizontalSpacing
    val verticalOffset = hexHeight * 0.52f + verticalSpacing

    Box(
        modifier = modifier.size(
            width = hexWidth * 2.8f + horizontalSpacing * 2,
            height = hexHeight * 3.2f + verticalSpacing * 2
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

        // Outer hexagons in honeycomb pattern with slight randomization
        if (outerLetters.size >= 6) {
            // Top
            HexagonButton(
                letter = outerLetters[0],
                isCenter = false,
                onClick = { onLetterClick(outerLetters[0]) },
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = randomOffsets[0].first,
                        y = -(hexHeight + verticalSpacing) + randomOffsets[0].second
                    )
            )

            // Top-right
            HexagonButton(
                letter = outerLetters[1],
                isCenter = false,
                onClick = { onLetterClick(outerLetters[1]) },
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = horizontalOffset + randomOffsets[1].first,
                        y = -verticalOffset + randomOffsets[1].second
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
                        x = horizontalOffset + randomOffsets[2].first,
                        y = verticalOffset + randomOffsets[2].second
                    )
            )

            // Bottom
            HexagonButton(
                letter = outerLetters[3],
                isCenter = false,
                onClick = { onLetterClick(outerLetters[3]) },
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = randomOffsets[3].first,
                        y = hexHeight + verticalSpacing + randomOffsets[3].second
                    )
            )

            // Bottom-left
            HexagonButton(
                letter = outerLetters[4],
                isCenter = false,
                onClick = { onLetterClick(outerLetters[4]) },
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = -horizontalOffset + randomOffsets[4].first,
                        y = verticalOffset + randomOffsets[4].second
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
                        x = -horizontalOffset + randomOffsets[5].first,
                        y = -verticalOffset + randomOffsets[5].second
                    )
            )
        }
    }
}
