package com.tifd.projectcomposed

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme

class ListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectComposeDTheme {
                MataKuliahListScreen()
            }
        }
    }
}

data class mataKuliah(
    val namaMatkul: String = "",
    val ruang: String = "",
    val hari: String = "",
    val jam: String = "",
    val is_praktikum: Boolean = false
)

@Composable
fun MataKuliahListScreen() {
    val db = Firebase.firestore
    var mataKuliahList by remember { mutableStateOf(emptyList<mataKuliah>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Log.d("ListActivity", "Starting Firestore fetch")
        isLoading = true
        db.collection("PAPBPraktikum").get().addOnSuccessListener { result ->
            Log.d("Firestore", "Successfully fetched documents: ${result.size()}")
            if (result.isEmpty) {
                Log.d("Firestore", "No documents found in 'PAPBPraktikum' collection")
                errorMessage = "No documents found."
            } else {
                mataKuliahList = result.documents.mapNotNull { document ->
                    try {
                        mataKuliah(
                            namaMatkul = document.getString("namaMatkul") ?: "",
                            ruang = document.getString("ruang") ?: "",
                            hari = document.getString("hari") ?: "",
                            jam = document.getString("jam") ?: "",
                            is_praktikum = document.getBoolean("is_praktikum") ?: false
                        )
                    } catch (e: Exception) {
                        Log.e("ListActivity", "Error fetching data: ${e.message}")
                        null
                    }
                }
            }
            isLoading = false
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error fetching documents: ${exception.message}")
            errorMessage = "Failed to fetch data: ${exception.message}"
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (mataKuliahList.isEmpty()) {
            Text(errorMessage ?: "No data found")
        } else {
            LazyColumn {
                items(mataKuliahList) { MataKuliah ->
                    OutlinedCardExample(MataKuliah)
                }
            }
        }
    }
}

@Composable
fun OutlinedCardExample(mataKuliah: mataKuliah) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Mata Kuliah: ${mataKuliah.namaMatkul}", style = MaterialTheme.typography.headlineSmall)
            Text(text = "Ruang: ${mataKuliah.ruang}")
            Text(text = "Hari: ${mataKuliah.hari}")
            Text(text = "Jam: ${mataKuliah.jam}")
            Text(text = "Praktikum: ${if (mataKuliah.is_praktikum) "Ya" else "Tidak"}")
        }
    }
}