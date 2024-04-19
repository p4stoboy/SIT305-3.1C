package com.example.quiz

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

class QuizViewModel : ViewModel() {
    private val questions = listOf(
        Question("What is 2 + 2?", listOf("3", "4", "5"), 1),
        Question(
            questionText = "What is the capital city of France?",
            options = listOf("Madrid", "Berlin", "Paris"),
            correctAnswerIndex = 2
        ),
        Question(
            questionText = "What gas do plants absorb from the atmosphere for photosynthesis?",
            options = listOf("Oxygen", "Carbon Dioxide", "Nitrogen"),
            correctAnswerIndex = 1
        ),
        Question(
            questionText = "Who was the first president of the United States?",
            options = listOf("Abraham Lincoln", "George Washington", "Thomas Jefferson"),
            correctAnswerIndex = 1
        ),
        Question(
            questionText = "Which country is known for the creation of the art form known as manga?",
            options = listOf("China", "Japan", "South Korea"),
            correctAnswerIndex = 1
        )
    )

    var currentQuestionIndex = mutableStateOf(0)
    var currentSelectedIndex = mutableStateOf(-1)
    var questionsSize = questions.size
    private var score = mutableStateOf(0)
    var userName = mutableStateOf("")

    fun submitAnswer(nc: NavController) {
        if (currentSelectedIndex.value == questions[currentQuestionIndex.value].correctAnswerIndex) {
            score.value++
        }
        nc.navigate("review")
    }

    fun nextQuestion(nc: NavController) {
        if (currentQuestionIndex.value < questionsSize - 1) {
            nc.navigate("quiz") {
                launchSingleTop = true
            }
            currentQuestionIndex.value++
            currentSelectedIndex.value = -1
        } else {
            nc.navigate("result")
        }
    }

    fun restartQuiz(nc: NavController) {
        currentQuestionIndex.value = 0
        currentSelectedIndex.value = -1
        score.value = 0
        nc.navigate("quiz")
    }

    fun getScore(): Int = score.value
    fun getCurrentQuestion(): Question = questions[currentQuestionIndex.value]
}
