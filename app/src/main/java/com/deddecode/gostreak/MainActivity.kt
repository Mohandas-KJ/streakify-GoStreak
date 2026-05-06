package com.deddecode.gostreak

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.compose.material.icons.filled.Delete
import android.widget.Toast

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF0F0F0F)
            ) {
                ZenStackApp()
            }
        }
    }
}

@Composable
fun ZenStackApp(zenViewModel: ZenViewModel = viewModel()) {

    val blocks = zenViewModel.blocks

    val streak by zenViewModel.streak

    val activity = androidx.compose.ui.platform.LocalContext.current as FragmentActivity

    val executor = ContextCompat.getMainExecutor(activity)

    val biometricPrompt = BiometricPrompt(
        activity,
        executor,

        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {

                super.onAuthenticationSucceeded(result)

                zenViewModel.clearAllBlocks()

                Toast.makeText(
                    activity,
                    "All Blocks Cleared",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Fingerprint Required")
        .setSubtitle("Authenticate to clear all streaks")
        .setNegativeButtonText("Cancel")
        .build()

    Scaffold(
        containerColor = Color.Transparent,

        floatingActionButton = {

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // CLEAR BUTTON
                FloatingActionButton(

                    onClick = {

                        biometricPrompt.authenticate(promptInfo)

                    },

                    containerColor = Color(0xFFFF5252)

                ) {

                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Clear",
                        tint = Color.White
                    )
                }

                // ADD BUTTON
                FloatingActionButton(

                    onClick = {
                        zenViewModel.addFocusBlock()
                    },

                    containerColor = Color(0xFFBB86FC)

                ) {

                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.Black
                    )
                }
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Text(
                "GO STREAK",
                fontSize = 12.sp,
                letterSpacing = 4.sp,
                color = Color.Gray
            )

            Text(
                text = "Current Streak: 🔥 $streak",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF252525)
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    val currentMinutes = zenViewModel.sliderPosition.floatValue.toInt()

                    Text(
                        text = "Next Session: $currentMinutes mins",
                        color = Color(0xFFBB86FC),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Slider(
                        value = zenViewModel.sliderPosition.floatValue,

                        onValueChange = {

                            // Snap to nearest 5
                            zenViewModel.sliderPosition.floatValue =
                                (it / 5).toInt() * 5f
                        },

                        valueRange = 5f..120f,

                        steps = 22,

                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFBB86FC),
                            activeTrackColor = Color(0xFFBB86FC)
                        )
                    )

                    Text(
                        text = "5 min → 120 min",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(blocks) { block ->
                    FocusCard(block)
                }
            }
        }
    }
}

@Composable
fun FocusCard(block: FocusBlock) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),

        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Block ${block.blockNumber}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = block.timeLabel,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Text(
                text = "COMPLETED",
                color = Color(0xFF03DAC5),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}