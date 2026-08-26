package com.github.ricardoalmeidas.to_do_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.ricardoalmeidas.to_do_list.navigation.AppNavigation
import com.github.ricardoalmeidas.to_do_list.ui.theme.TodolistTheme
import com.github.ricardoalmeidas.to_do_list.viewmodel.TarefaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodolistTheme {
                // A mesma instancia e compartilhada pelas duas telas.
                val viewModel: TarefaViewModel = viewModel(
                    factory = TarefaViewModel.factory(applicationContext)
                )
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
