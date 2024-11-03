package com.tifd.projectcomposed.screen

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tifd.projectcomposed.R
import com.tifd.projectcomposed.viewmodel.MainViewModel
import com.tifd.projectcomposed.viewmodel.MainViewModelFactory
import com.tifd.projectcomposed.local.Tugas

@Composable
fun TugasScreen() {  // No need to pass TugasRepository
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(application)
    )

    var matkul by remember { mutableStateOf("") }
    var detailTugas by remember { mutableStateOf("") }
    val tugasList by mainViewModel.tugasList.observeAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = matkul,
            onValueChange = { matkul = it },
            label = { Text("Matkul") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = detailTugas,
            onValueChange = { detailTugas = it },
            label = { Text("Detail Tugas") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                mainViewModel.addTugas(matkul, detailTugas)
                matkul = ""
                detailTugas = ""
            }) {
                Text("Add Tugas")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(tugasList) { tugas ->
                TugasCard(tugas) { updatedTugas ->
                    mainViewModel.updateTugas(updatedTugas)
                }
            }
        }
    }
}

@Composable
fun TugasCard(tugas: Tugas, onTaskCompleted: (Tugas) -> Unit){
    var isCompleted by remember { mutableStateOf(tugas.selesai) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column {
                Text(text = tugas.matkul)
                Text(text = tugas.detailTugas)
            }

            IconButton(onClick = {
                isCompleted = !isCompleted
                onTaskCompleted(tugas.copy(selesai = isCompleted))

            }) {

                Icon(
                    painter = painterResource(id = if (isCompleted) R.drawable.baseline_check_circle_24 else R.drawable.baseline_cancel_24),
                    contentDescription = if (isCompleted) "Task Completed" else "Task Incomplete",
                )

            }
        }
    }
}