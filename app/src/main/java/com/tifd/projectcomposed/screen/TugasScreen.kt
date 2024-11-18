package com.tifd.projectcomposed.screen

import android.Manifest
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tifd.projectcomposed.R
import com.tifd.projectcomposed.viewmodel.MainViewModel
import com.tifd.projectcomposed.viewmodel.MainViewModelFactory
import com.tifd.projectcomposed.local.Tugas
import com.tifd.projectcomposed.ui.theme.Pink80
import com.tifd.projectcomposed.ui.theme.PurpleGrey80

@Composable
fun TugasScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(application)
    )
    var matkul by remember { mutableStateOf("") }
    var detailTugas by remember { mutableStateOf("") }
    var capturedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val tugasList by mainViewModel.tugasList.observeAsState(initial = emptyList())
    var showCamera by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surface)
                )
            )
    ) {
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

            if (!showCamera) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        if (matkul.isNotBlank() && detailTugas.isNotBlank()) {
                            mainViewModel.addTugas(
                                matkul = matkul,
                                detailTugas = detailTugas,
                                photoUri = capturedPhotoUri?.toString() // Add photo if available
                            )
                            matkul = ""
                            detailTugas = ""
                            capturedPhotoUri = null
                        } else {
                            Toast.makeText(
                                context,
                                "Matkul and Detail Tugas cannot be empty!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurpleGrey80,
                        contentColor = Color.White)
                    ) {
                        Text("Add Tugas")
                    }

                    Button(onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            showCamera = true
                        } else {
                            Toast.makeText(
                                context,
                                "Camera permission required!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                            containerColor = PurpleGrey80,
                            contentColor = Color.White)
                    ) {
                        Text("Open Camera")
                    }
                }

                // Preview of the captured photo (if available)
                capturedPhotoUri?.let { uri ->
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = uri,
                        contentDescription = "Captured Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            } else {
                // Show CameraXPreview
                CameraXPreview(
                    onImageCaptured = { imageUri ->
                        capturedPhotoUri = imageUri
                        showCamera = false // Close camera after capturing photo
                    },
                    onCloseCamera = { showCamera = false }
                )
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
}

@Composable
fun TugasCard(tugas: Tugas, onTaskCompleted: (Tugas) -> Unit) {
    var isCompleted by remember { mutableStateOf(tugas.selesai) }

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = tugas.matkul, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp))
                HorizontalDivider(
                    thickness = 3.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = tugas.detailTugas, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp))

                if (!tugas.photoUri.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = tugas.photoUri,
                        contentDescription = "Task Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }
            }

            IconButton(onClick = {
                isCompleted = !isCompleted
                onTaskCompleted(tugas.copy(selesai = isCompleted))
            }) {
                Icon(
                    painter = painterResource(
                        id = if (isCompleted) R.drawable.baseline_check_circle_24
                        else R.drawable.baseline_cancel_24
                    ),
                    contentDescription = if (isCompleted) "Task Completed" else "Task Incomplete"
                )
            }
        }
    }
}

@Composable
fun CameraXPreview(onImageCaptured: (Uri) -> Unit, onCloseCamera: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        imageCapture = ImageCapture.Builder().build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageCapture
            )
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            Button(
                onClick = {
                    takePhoto(context, imageCapture) { uri ->
                        onImageCaptured(uri)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Pink80,
                    contentColor = Color.Black
                )
            ) {
                Text("Take Photo")
            }

            Button(
                onClick = { onCloseCamera() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleGrey80,
                    contentColor = Color.Black
                )
            ) {
                Text("Close Camera")
            }
        }
    }
}

fun takePhoto(context: Context, imageCapture: ImageCapture?, onImageCaptured: (Uri) -> Unit) {
    val fileName = "${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TugasApp")
        }
    }
    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        .build()

    imageCapture?.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onImageCaptured(output.savedUri!!)
            }

            override fun onError(exc: ImageCaptureException) {
                Toast.makeText(context, "Failed to capture image: ${exc.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}