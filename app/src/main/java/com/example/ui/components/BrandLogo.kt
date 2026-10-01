package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

@Composable
fun BrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    elevation: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation, RoundedCornerShape(size * 0.25f))
            .clip(RoundedCornerShape(size * 0.25f))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_numerix_logo),
            contentDescription = "Numerix Logo",
            modifier = Modifier.size(size * 0.85f)
        )
    }
}
