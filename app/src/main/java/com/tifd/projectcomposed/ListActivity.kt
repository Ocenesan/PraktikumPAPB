package com.tifd.projectcomposed

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.painterResource
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
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp) // Menambahkan sedikit bayangan
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Mata Kuliah Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_book_24), // Drawable for mata kuliah
                    contentDescription = "Mata Kuliah",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = mataKuliah.namaMatkul,
                    style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), thickness = 1.dp)

            // Ruang
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_location_on_24), // Drawable for room
                    contentDescription = "Ruang",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ruang: ${mataKuliah.ruang}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Hari
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_calendar_month_24), // Drawable for day
                    contentDescription = "Hari",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hari: ${mataKuliah.hari}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Jam
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_access_time_24), // Drawable for time
                    contentDescription = "Jam",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Jam: ${mataKuliah.jam}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Praktikum
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(
                    painter = painterResource(id = if (mataKuliah.is_praktikum) R.drawable.baseline_check_circle_24 else R.drawable.baseline_cancel_24), // Drawable for praktikum
                    contentDescription = "Praktikum",
                    tint = if (mataKuliah.is_praktikum) Color.Green else Color.Red,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (mataKuliah.is_praktikum) "Praktikum" else "Praktikum",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}