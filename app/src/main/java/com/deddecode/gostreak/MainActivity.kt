package com.deddecode.gostreak

import android.os.Bundle
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.activity.compose.setContent
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ZenStackApp()
        }
    }
}

@Composable
fun ZenStackApp(zenViewModel: ZenViewModel = viewModel()) {

    val blocks = zenViewModel.blocks

    val streak by zenViewModel.streak

    val activity =
        androidx.compose.ui.platform.LocalContext.current as FragmentActivity

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
                    "All Blocks Cleared 🔥",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Fingerprint Required")
        .setSubtitle("Authenticate to clear streak")
        .setNegativeButtonText("Cancel")
        .build()

    // FAB Pulse Animation
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val fabScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,

        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),

        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                )
            )
    ) {

        Scaffold(
            containerColor = Color.Transparent,

            floatingActionButton = {

                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // CLEAR FAB
                    FloatingActionButton(
                        onClick = {
                            biometricPrompt.authenticate(promptInfo)
                        },

                        containerColor = Color(0xFFFF5252),

                        modifier = Modifier
                            .scale(fabScale)
                            .shadow(16.dp, CircleShape)
                    ) {

                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White
                        )
                    }

                    // ADD FAB
                    FloatingActionButton(
                        onClick = {
                            zenViewModel.addFocusBlock()
                        },

                        containerColor = Color(0xFFBB86FC),

                        modifier = Modifier
                            .scale(fabScale)
                            .shadow(16.dp, CircleShape)
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

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "GO STREAK",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    letterSpacing = 5.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // STREAK CARD
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(28.dp)
                        ),

                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1F2937)
                    ),

                    shape = RoundedCornerShape(28.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(42.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {

                                Text(
                                    text = "$streak DAY STREAK",
                                    color = Color.White,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Consistency is Power ⚡",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // SESSION SLIDER CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.08f)
                    ),

                    shape = RoundedCornerShape(24.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(22.dp)
                    ) {

                        val currentMinutes =
                            zenViewModel.sliderPosition.floatValue.toInt()

                        Text(
                            text = "NEXT SESSION",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            letterSpacing = 3.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "$currentMinutes Minutes",
                            color = Color(0xFFBB86FC),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Slider(
                            value = zenViewModel.sliderPosition.floatValue,

                            onValueChange = {

                                zenViewModel.sliderPosition.floatValue =
                                    (it / 5).toInt() * 5f
                            },

                            valueRange = 5f..120f,

                            steps = 0,

                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFBB86FC),
                                activeTrackColor = Color(0xFFBB86FC),
                                inactiveTrackColor = Color.Gray
                            )
                        )

                        Text(
                            text = "5 min → 120 min",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "TODAY'S SESSIONS",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    letterSpacing = 3.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {

                    items(blocks) { block ->

                        FocusCard(block)
                    }
                }
            }
        }
    }
}

@Composable
fun FocusCard(block: FocusBlock) {

    val cardColors = listOf(
        Brush.horizontalGradient(
            listOf(
                Color(0xFF7F00FF),
                Color(0xFFE100FF)
            )
        ),

        Brush.horizontalGradient(
            listOf(
                Color(0xFF11998E),
                Color(0xFF38EF7D)
            )
        ),

        Brush.horizontalGradient(
            listOf(
                Color(0xFFFF512F),
                Color(0xFFF09819)
            )
        )
    )

    val brush = remember {
        cardColors.random()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp)
            ),

        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),

        shape = RoundedCornerShape(24.dp)
    ) {

        Box(
            modifier = Modifier
                .background(brush)
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.padding(18.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                NeonCircleIcon(
                    icon = Icons.Default.LocalFireDepartment
                )

                Spacer(modifier = Modifier.width(18.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Block ${block.blockNumber}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = block.timeLabel,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "DONE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun NeonCircleIcon(icon: ImageVector) {

    Box(
        modifier = Modifier
            .size(54.dp)
            .background(
                Color.White.copy(alpha = 0.2f),
                CircleShape
            ),

        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}