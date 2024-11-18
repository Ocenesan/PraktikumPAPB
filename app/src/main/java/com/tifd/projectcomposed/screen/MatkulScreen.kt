package com.tifd.projectcomposed.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tifd.projectcomposed.R
import com.tifd.projectcomposed.ui.theme.LightPurple

data class mataKuliah(
    val namaMatkul: String = "",
    val ruang: String = "",
    val hari: String = "",
    val jam: String = "",
    val is_praktikum: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatkulScreen() {
    val db = Firebase.firestore
    var mataKuliahList by remember { mutableStateOf(emptyList<mataKuliah>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    //val context = LocalContext.current

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Jadwal Mata Kuliah", style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold) }
            )
        }
    ){ innerPadding ->  // Use innerPadding to avoid content being obscured by system bars
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Loading...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else if (mataKuliahList.isEmpty()) {
                Text(
                    text = errorMessage ?: "No data found",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        ),
                    contentPadding = PaddingValues(16.dp) // Consistent padding
                ) {
                    items(mataKuliahList) { MataKuliah ->
                        OutlinedCardExample(MataKuliah)
                        Spacer(modifier = Modifier.height(8.dp)) // Spacing between cards
                    }
                }
            }
        }
    }
}

@Composable
fun OutlinedCardExample(mataKuliah: mataKuliah) {
    // State to manage the expanded state of the card
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable { expanded = !expanded }, // Toggle expansion on click
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_book_24),
                    contentDescription = "Mata Kuliah",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = mataKuliah.namaMatkul,
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.padding(8.dp)
                )
            }

            if (expanded) {
                // Expanded content
                HorizontalDivider(
                    thickness = 3.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.Start) { // Hari & Ruang in a Column

                        Row(verticalAlignment = Alignment.CenterVertically) {  // Hari
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                contentDescription = "Hari",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = mataKuliah.hari,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black,
                                modifier = Modifier
                                    .background(LightPurple, shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {  // Ruang
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_location_on_24),
                                contentDescription = "Ruang",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = mataKuliah.ruang,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black,
                                modifier = Modifier
                                    .background(LightPurple, shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {  // Jam
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_access_time_24),
                            contentDescription = "Jam",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Jam: ${mataKuliah.jam}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            modifier = Modifier
                                .background(LightPurple, shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    Icon(
                        painter = painterResource(id = if (mataKuliah.is_praktikum) R.drawable.baseline_check_circle_24 else R.drawable.baseline_cancel_24), // Drawable for praktikum
                        contentDescription = "Praktikum",
                        tint = if (mataKuliah.is_praktikum) Color.Green else Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (mataKuliah.is_praktikum) "Praktikum" else "Non-Praktikum",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        modifier = Modifier
                            .background(LightPurple, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}