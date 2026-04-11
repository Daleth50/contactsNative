package com.example.contacts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
private val avatarColors = listOf(
    Color(0xFF6750A4), Color(0xFF006E1C), Color(0xFF9A4521),
    Color(0xFF006A6A), Color(0xFF005FAF), Color(0xFF8B4000),
    Color(0xFF9A0007), Color(0xFF6B5778)
)
@Composable
fun ContactAvatar(name: String, lastName: String, size: Int = 48, fontSize: Int = 18) {
    val initials = buildString {
        if (name.isNotEmpty()) append(name.first().uppercaseChar())
        if (lastName.isNotEmpty()) append(lastName.first().uppercaseChar())
    }
    val avatarColor = avatarColors[abs(name.hashCode()) % avatarColors.size]

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(avatarColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize.sp
        )
    }
}