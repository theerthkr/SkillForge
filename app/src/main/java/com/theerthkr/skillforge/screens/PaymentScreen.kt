package com.theerthkr.skillforge.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.theerthkr.skillforge.data.repository.SkillforgeRepository
import com.theerthkr.skillforge.ui.theme.CreamBackground
import com.theerthkr.skillforge.ui.theme.DarkTealPrimary

// Visual transformation that inserts a space every 4 digits: 1234 5678 9012 3456
private class CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val sb = StringBuilder()
        for (i in raw.indices) {
            if (i > 0 && i % 4 == 0) sb.append(' ')
            sb.append(raw[i])
        }
        val formatted = sb.toString()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val spacesBeforeOffset = (offset - 1) / 4
                return offset + spacesBeforeOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                var original = 0
                var transformed = 0
                while (transformed < offset && original < raw.length) {
                    original++
                    transformed++
                    if (original % 4 == 0 && original < raw.length) transformed++ // skip space
                }
                return original.coerceAtMost(raw.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

// Visual transformation that inserts a slash after MM: 12/34 → MM/YY
private class ExpiryVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val sb = StringBuilder()
        for (i in raw.indices) {
            if (i == 2) sb.append('/')
            sb.append(raw[i])
        }
        val formatted = sb.toString()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return if (offset <= 2) offset else offset + 1
            }

            override fun transformedToOriginal(offset: Int): Int {
                return if (offset <= 2) offset else (offset - 1).coerceAtMost(raw.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    courseId: String,
    onBackClick: () -> Unit
) {
    // Raw digits only — no spaces, no slashes stored in state
    var cardNumber by remember { mutableStateOf("") }   // max 16 digits
    var expiry by remember { mutableStateOf("") }        // max 4 digits (MMYY)
    var cvv by remember { mutableStateOf("") }           // max 4 digits
    var isSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val repository = remember { SkillforgeRepository.instance }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = DarkTealPrimary,
        unfocusedBorderColor = Color(0xFFDCDCDC),
        focusedLabelColor = DarkTealPrimary
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    CircleIconButton(
                        icon = Icons.Filled.ArrowBack,
                        onClick = onBackClick,
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF1A1A1A)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamBackground),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = CreamBackground
    ) { padding ->
        if (isSuccess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.CreditCard,
                        contentDescription = "Success",
                        tint = DarkTealPrimary,
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Payment Successful!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You are now enrolled in the course.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF8A8A8A)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = onBackClick,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkTealPrimary)
                    ) {
                        Text("Return to Course")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Payment Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Card Number — digits only, max 16, displayed as XXXX XXXX XXXX XXXX
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { newValue ->
                        val digits = newValue.filter { it.isDigit() }
                        if (digits.length <= 16) cardNumber = digits
                    },
                    label = { Text("Card Number") },
                    placeholder = { Text("1234 5678 9012 3456", color = Color(0xFFB0B0B0)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    visualTransformation = CardNumberVisualTransformation()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    // Expiry — digits only, max 4, displayed as MM/YY
                    OutlinedTextField(
                        value = expiry,
                        onValueChange = { newValue ->
                            val digits = newValue.filter { it.isDigit() }
                            if (digits.length <= 4) expiry = digits
                        },
                        label = { Text("MM/YY") },
                        placeholder = { Text("12/28", color = Color(0xFFB0B0B0)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        visualTransformation = if (expiry.length > 2) ExpiryVisualTransformation() else VisualTransformation.None
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // CVV — digits only, max 4
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { newValue ->
                            val digits = newValue.filter { it.isDigit() }
                            if (digits.length <= 4) cvv = digits
                        },
                        label = { Text("CVV") },
                        placeholder = { Text("123", color = Color(0xFFB0B0B0)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFDC2626),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        // All fields required
                        if (cardNumber.isBlank() || expiry.isBlank() || cvv.isBlank()) {
                            errorMessage = "Please fill in all payment details."
                            return@Button
                        }

                        // Card number: must be exactly 13–16 digits
                        if (cardNumber.length < 13) {
                            errorMessage = "Please enter a valid card number (13–16 digits)."
                            return@Button
                        }

                        // Expiry: must be exactly 4 digits (MMYY)
                        if (expiry.length != 4) {
                            errorMessage = "Please enter a valid expiry date (MM/YY)."
                            return@Button
                        }
                        val month = expiry.substring(0, 2).toIntOrNull()
                        val year = expiry.substring(2, 4).toIntOrNull()
                        if (month == null || year == null || month !in 1..12) {
                            errorMessage = "Please enter a valid month (01–12)."
                            return@Button
                        }
                        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100
                        val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
                        if (year < currentYear || (year == currentYear && month < currentMonth)) {
                            errorMessage = "Your card has expired."
                            return@Button
                        }

                        // CVV: 3 or 4 digits
                        if (cvv.length < 3) {
                            errorMessage = "Please enter a valid CVV (3 or 4 digits)."
                            return@Button
                        }

                        errorMessage = ""
                        repository.unlockCourse(courseId)
                        isSuccess = true
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkTealPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Pay Now",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
