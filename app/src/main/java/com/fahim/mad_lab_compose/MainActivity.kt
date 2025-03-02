package com.fahim.mad_lab_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fahim.mad_lab_compose.data.entity.Task
import com.fahim.mad_lab_compose.ui.theme.MADLabComposeTheme
import com.fahim.mad_lab_compose.viewmodel.TaskViewModel
import com.fahim.mad_lab_compose.viewmodel.TaskViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val taskViewModel: TaskViewModel = viewModel(
                factory = TaskViewModelFactory(
                    (application as TaskApplication).repository
                )
            )

            MADLabComposeTheme {
                ToDoApp(taskViewModel)

            }
        }
    }
}

@Composable
fun ToDoApp(taskViewModel: TaskViewModel) {
    val tasks by taskViewModel.allTask.collectAsState(initial = emptyList())

    Column {
        TaskList(tasks = tasks, onDelete = { task ->
            taskViewModel.delete(task)
        })
//        AddTaskForm(taskViewModel = taskViewModel)
    }
}

@Composable
fun TaskList(tasks: List<Task>, onDelete: (Task) -> Unit) {
    LazyColumn {
        items(tasks) { task ->
            TaskItem(task = task, onDelete = onDelete)
        }

    }
}

@Composable
fun TaskItem(task: Task, onDelete: (Task) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = task.title)
                Text(text = task.description)

            }
            IconButton(onClick = { onDelete(task) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Preview
@Composable
private fun TaskItemPreview() {
    TaskItem(
        task = Task(
            1, "Title", "Description"
        ), onDelete = {}
    )
}