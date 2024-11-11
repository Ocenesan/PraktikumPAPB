package com.tifd.projectcomposed.screen

import android.Manifest
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
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
                }
            }) {
                Text(if (showCamera) "Close Camera" else "Open Camera")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showCamera) {
            CameraXPreview(onImageCaptured = { imageUri ->
                // Handle hasil foto, bisa disimpan ke ViewModel atau lokasi lain
                showCamera = false // Menutup kamera setelah foto diambil

                // Contoh: Menampilkan URI gambar dalam Toast
                Toast.makeText(context, "Gambar disimpan di: $imageUri", Toast.LENGTH_LONG).show()
            })
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
fun CameraXPreview(onImageCaptured: (Uri) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }

    LaunchedEffect(cameraProviderFuture, lensFacing) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

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

    // Launcher untuk meminta izin kamera
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { granted ->
            val cameraGranted = granted.getOrDefault(Manifest.permission.CAMERA, false)
            val storageGranted = granted.getOrDefault(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)
            if (cameraGranted && storageGranted) {
                // Izin diberikan
                Log.d("CameraContent", "Izin diberikan") // Tambahkan logging
            } else {
                Toast.makeText(context, "Izin Kamera dan Penyimpanan dibutuhkan", Toast.LENGTH_SHORT).show()
                Log.d("CameraContent", "Izin ditolak") // Tambahkan logging
            }
        }
    )

    Column {
        Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .size(width = 300.dp, height = 150.dp)
                    .align(Alignment.Center)
            )
            Box(modifier = Modifier.fillMaxWidth().align(Alignment.TopEnd)) {
                IconButton(
                    onClick = {
                        lensFacing =
                            if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT
                            else CameraSelector.LENS_FACING_BACK
                    },
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(if (lensFacing == CameraSelector.LENS_FACING_BACK) R.drawable.baseline_flip_camera_ios_24 else R.drawable.baseline_flip_camera_ios_24),
                        contentDescription = "Flip Camera",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            if (imageCapture != null) {
                Box(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)) {
                    Button(onClick = {
                        launcher.launch(
                            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        )
                        takePhoto(context, imageCapture, onImageCaptured)
                    },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_radio_button_checked_32), // Ganti dengan drawable Anda
                            contentDescription = "Take Photo",
                            modifier = Modifier.size(32.dp) // Sesuaikan ukuran
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Take Photo")
                    }
                }
            }
        }
    }
}

fun takePhoto(context: Context, imageCapture: ImageCapture?, onImageCaptured: (Uri) -> Unit) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
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
        object: ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onImageCaptured(output.savedUri!!)
            }
            override fun onError(exc: ImageCaptureException) {
                Toast.makeText(context, "Gagal mengambil gambar: ${exc.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}