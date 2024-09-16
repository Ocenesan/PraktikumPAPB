package com.tifd.projectcomposed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

@Composable
fun MyScreen() {
    var text by remember { mutableStateOf("") }
    var namaText by remember { mutableStateOf("") }
    var numText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isFormVisible by remember { mutableStateOf(true) } // State baru untuk kontrol visibilitas

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
            Button(onClick = {
                if (namaText.isBlank() || numText.isBlank()) {
                    errorMessage = "Tolong isi semua field"
                } else {
                    text = "Nama : $namaText\n NIM : $numText"
                    errorMessage = null // Menghapus pesan kesalahan jika ada
                    //isFormVisible = false // Menghilangkan TextField dan Button
                }
            }) {
                Text("Submit")
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
