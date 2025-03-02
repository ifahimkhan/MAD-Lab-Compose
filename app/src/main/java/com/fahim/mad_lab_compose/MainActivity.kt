package com.fahim.mad_lab_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

            val tasks by taskViewModel.allTask.collectAsState(initial = emptyList())

            MADLabComposeTheme {
                ToDoApp(tasks = tasks,
                    onAdd = { title, description ->
                        taskViewModel.insert(
                            Task(
                                title = title,
                                description = description
                            )
                        )
                    },
                    onDelete = { task ->
                        taskViewModel.delete(task)
                    })

            }
            }
        }
}

@Composable
fun ToDoApp(tasks: List<Task>, onAdd: (String, String) -> Unit, onDelete: (task: Task) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp)

    ) {
        AddTaskForm(onAdd = { title, description ->
            onAdd(title, description)
        })
        TaskList(tasks = tasks, onDelete = { task ->
            onDelete(task)
        })

    }
}

@Composable
fun AddTaskForm(onAdd: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var enableButton by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                enableButton = title.isNotEmpty() && description.isNotEmpty()

            },
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                enableButton = title.isNotEmpty() && description.isNotEmpty()
            },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        Button(
            onClick = {
                onAdd(title, description)
            }, modifier = Modifier.padding(4.dp),
            enabled = enableButton
        ) {
            Text("Add Task")
        }
    }
}

@Composable
fun TaskList(tasks: List<Task>, onDelete: (Task) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(fraction = 1f)
            .scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollState()
            )
    ) {
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

@Preview
@Composable
private fun AddTaskPreview() {
    AddTaskForm(onAdd = { _, _ -> })
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TodoAppPreview() {
    val mockTasks = listOf(
        Task(id = 1, title = "Task 1", description = "Description 1"),
        Task(id = 2, title = "Task 2", description = "Description 2"),
        Task(id = 3, title = "Task 2", description = "Description 2"),
        Task(id = 4, title = "Task 2", description = "Description 2"),
        Task(id = 5, title = "Task 2", description = "Description 2"),
        Task(id = 6, title = "Task 2", description = "Description 2"),
        Task(id = 7, title = "Task 2", description = "Description 2"),
        Task(id = 8, title = "Task 2", description = "Description 2")
    )

    ToDoApp(tasks = mockTasks,
        onAdd = { _, _ -> },
        onDelete = { _ -> })
}