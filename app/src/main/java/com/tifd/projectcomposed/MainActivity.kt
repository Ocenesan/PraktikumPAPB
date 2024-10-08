@file:OptIn(ExperimentalFoundationApi::class)

package com.tifd.projectcomposed

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        if (auth.currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            setContent {
                ProjectComposeDTheme (dynamicColor = false){
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MyScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MyScreen() {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var namaText by remember { mutableStateOf("") }
    var numText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isFormVisible by remember { mutableStateOf(true) } // State baru untuk kontrol visibilitas
    val isButtonEnabled = namaText.isNotBlank() && numText.isNotBlank() // Kondisi button aktif

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Text(text = text)
        Spacer(modifier = Modifier.height(16.dp))

        if (isFormVisible) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = "Person Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                OutlinedTextField(
                    value = namaText,
                    onValueChange = { namaText = it },
                    label = { Text("Masukkan nama") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_123_24),
                    contentDescription = "Number Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                OutlinedTextField(
                    value = numText,
                    onValueChange = {
                        val filtered = it.filter { char -> char.isDigit() }
                        numText = filtered
                    },
                    label = { Text("Masukkan NIM") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Button(
                onClick = {
                    if (namaText.isBlank() || numText.isBlank()) {
                        errorMessage = "Tolong isi semua field"
                    } else {
                        text = "Nama : $namaText\n NIM : $numText"
                        errorMessage = null
                    }
                },
                enabled = isButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isButtonEnabled) MaterialTheme.colorScheme.primary else Color.Gray
                )
            ) {
                Box(
                    modifier = Modifier.combinedClickable(
                        onClick = {},
                        onLongClick = {
                            Toast.makeText(context, "Nama: $namaText\nNIM: $numText", Toast.LENGTH_SHORT).show()
                        }
                    )
                ) {
                    Text("Submit")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProjectComposeDTheme {
        MyScreen()
    }
}
