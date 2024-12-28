package com.example.sandhedellerbullshit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import kotlin.random.Random

data class Question(val cardId: String, val questions: String, val answers: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SandhedEllerBullshitTheme {
                SandhedEllerBullshitScreen()
            }
        }
    }
}

@Composable
fun SandhedEllerBullshitScreen() {
    var questionList by remember { mutableStateOf<List<Question>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var flipped by remember { mutableStateOf(false) }

    // Load the questions from the local JSON file
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        questionList = loadQuestionsFromAsset(context)
    }

    if (questionList.isEmpty()) {
        return
    }

    // Flip animation state
    val flipAngle by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        label = "flip"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Dynamic background color
            .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            )  {
            // Question Card
            QuestionCard(
                question = questionList[currentIndex],
                flipped = flipped,
                flipAngle = flipAngle,
                onCardClick = { flipped = !flipped }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Random button
            Button(
                onClick = {
                    currentIndex = Random.nextInt(questionList.size)
                    flipped = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Replaces backgroundColor
                    contentColor = MaterialTheme.colorScheme.onPrimary  // Replaces contentColor
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Random Question")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = {
                    if (currentIndex > 0) currentIndex--
                    flipped = false
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary, // Replaces backgroundColor
                        contentColor = MaterialTheme.colorScheme.onPrimary  // Replaces contentColor
                    ),
                    ) {
                    Text("Previous")
                }

                Button(onClick = {
                    if (currentIndex < questionList.size - 1) currentIndex++
                    flipped = false
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary, // Replaces backgroundColor
                        contentColor = MaterialTheme.colorScheme.onPrimary  // Replaces contentColor
                    ),
                ) {
                    Text("Next")
                }
            }
        }
    }
}

@Composable
fun QuestionCard(
    question: Question,
    flipped: Boolean,
    flipAngle: Float,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount > 0) onCardClick() // Flip card on swipe right
                }
            }
            .graphicsLayer(
                rotationY = flipAngle,
                cameraDistance = 12f
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onCardClick)
        ) {
            // Front of the card (Question)
            if (!flipped) {
                Text(
                    text = question.questions,
                    style = TextStyle(fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .graphicsLayer {
                            rotationY = 0f // Ensure normal text direction
                        }
                )
            }
            // Back of the card (Answer)
            else {
                Text(
                    text = question.answers,
                    style = TextStyle(fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .graphicsLayer {
                            rotationY = 180f // Flip text to counteract card flip
                        }
                )
            }
        }
    }
}

fun loadQuestionsFromAsset(context: android.content.Context): List<Question> {
    val inputStream = context.assets.open("q_And_A.json")
    val reader = InputStreamReader(inputStream)
    val questionType = object : TypeToken<List<Question>>() {}.type
    return Gson().fromJson(reader, questionType)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SandhedEllerBullshitScreen()
}
