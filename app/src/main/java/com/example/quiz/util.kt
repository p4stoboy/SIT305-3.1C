package com.example.quiz

import android.app.Activity
import android.util.Log
import android.widget.ProgressBar
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

data class Question(
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

@Composable
fun HomeScreen(nc: NavController, viewModel: QuizViewModel) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        val textState = remember { mutableStateOf("") }

        OutlinedTextField(
            value = textState.value,
            onValueChange = { newText: String -> textState.value = newText },
            label = { Text("Enter your name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            viewModel.userName.value = textState.value
            viewModel.restartQuiz(nc)
        }) {
            Text("Start Quiz")
        }
    }
}

@Composable
fun QuizScreen(nc: NavController, viewModel: QuizViewModel) {
    val currentQuestion = viewModel.getCurrentQuestion()
    Column {
        Text(text = "Question ${viewModel.currentQuestionIndex.value + 1} of ${viewModel.questionsSize}")
        LinearProgressIndicator(progress = viewModel.currentQuestionIndex.value.toFloat() / viewModel.questionsSize, modifier = Modifier.fillMaxWidth())
        Text(text = "Name: ${viewModel.userName.value}")
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = currentQuestion.questionText)
            for (i in currentQuestion.options.indices) {
                val selected = viewModel.currentSelectedIndex.value == i
                var col = if (selected) Color(0xFF444444) else Color(0xFFFFFFFF)
                var concol = if (selected) Color(0xFFFFFFFF) else Color(0xFF000000)
                OutlinedButton(colors = ButtonDefaults.buttonColors(containerColor = col, contentColor = concol),
                    onClick = {
                    viewModel.currentSelectedIndex.value = i
                }) {
                    Text(currentQuestion.options[i])
                }
            }
            Button(onClick = {
                if (viewModel.currentSelectedIndex.value > -1) {
                    viewModel.submitAnswer(nc)
                }
            }) {
                Text("Submit")
            }
        }
    }
}

@Composable
fun ReviewScreen(nc: NavController, viewModel: QuizViewModel) {
    val currentQuestion = viewModel.getCurrentQuestion()
    // need to mutate this down below so that the current question on the next Quiz Screen is not highlighted
    var correctCol = remember { mutableStateOf( Color.Green ) }
    val wrongCol = Color.Red
    val choice = viewModel.currentSelectedIndex.value
    Column {
        Text(text = "Question ${viewModel.currentQuestionIndex.value + 1} of ${viewModel.questionsSize}")
        LinearProgressIndicator(progress = viewModel.currentQuestionIndex.value.toFloat() / viewModel.questionsSize, modifier = Modifier.fillMaxWidth())
        Text(text = "Name: ${viewModel.userName.value}")
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = currentQuestion.questionText)
            for (i in currentQuestion.options.indices) {
                val col = if (i == currentQuestion.correctAnswerIndex) correctCol.value else if (choice == i) wrongCol else Color(0xFFFFFFFF)
                val concol = Color(0xFF000000)
                OutlinedButton(colors = ButtonDefaults.buttonColors(containerColor = col, contentColor = concol), onClick = {}) {
                    Text(currentQuestion.options[i])
                }
            }
            Button(onClick = {
                // reset the color of the correct answer in case currentQuestionIndex increments before navigation
                correctCol.value = Color(0xFFFFFFFF)
                viewModel.nextQuestion(nc)
            }) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun ResultScreen(nc: NavController, viewModel: QuizViewModel) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Your score: ${viewModel.getScore()}")
        Button(onClick = { viewModel.restartQuiz(nc) }) {
            Text("New Quiz")
        }
        FinishButton()
    }
}

@Composable
fun FinishButton() {
    val context = LocalContext.current
    Button(onClick = {
        if (context is Activity) {
            context.finish()
        }
    }) {
        Text("Finish")
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val viewModel = QuizViewModel()
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController, viewModel) }
        composable("quiz") { QuizScreen(navController, viewModel) }
        composable("review") { ReviewScreen(navController, viewModel) }
        composable("result") { ResultScreen(navController, viewModel) }
    }
}