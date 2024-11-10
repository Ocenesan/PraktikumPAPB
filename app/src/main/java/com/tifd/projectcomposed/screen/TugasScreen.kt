package com.tifd.projectcomposed.screen

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tifd.projectcomposed.R
import com.tifd.projectcomposed.viewmodel.MainViewModel
import com.tifd.projectcomposed.viewmodel.MainViewModelFactory
import com.tifd.projectcomposed.local.Tugas

@Composable
fun TugasScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(application)
    )

    var matkul by remember { mutableStateOf("") }
    var detailTugas by remember { mutableStateOf("") }
    val tugasList by mainViewModel.tugasList.observeAsState(initial = emptyList())
    var showCamera by remember { mutableStateOf(false) }

    // Launcher untuk meminta izin kamera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            showCamera = isGranted // Tampilkan kamera jika izin diberikan
        }
    )

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

            Button(onClick = {
                // Cek izin kamera sebelum menampilkan kamera
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    showCamera = true
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }) {
                Text(if (showCamera) "Close Camera" else "Open Camera")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showCamera) {
            CameraXPreview() // Tampilkan pratinjau kamera
        }

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
fun TugasCard(tugas: Tugas, onTaskCompleted: (Tugas) -> Unit) {
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

@Composable
fun CameraXPreview() {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = androidx.camera.core.Preview.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        preview.setSurfaceProvider(previewView.surfaceProvider)

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    )
}