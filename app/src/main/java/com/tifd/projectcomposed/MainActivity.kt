package com.tifd.projectcomposed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tifd.projectcomposed.Navigation.Navigationitem
import com.tifd.projectcomposed.Navigation.Screen
import com.tifd.projectcomposed.screen.LoginScreen
import com.tifd.projectcomposed.screen.MatkulScreen
import com.tifd.projectcomposed.screen.ProfileScreen
import com.tifd.projectcomposed.screen.TugasScreen
import com.tifd.projectcomposed.ui.theme.*

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        setContent {
            ProjectComposeDTheme {
                val navController = rememberNavController()
                // Check if user is authenticated
                val isUserLoggedIn = remember { mutableStateOf(auth.currentUser != null) }

                // Show different content based on login status
                if (isUserLoggedIn.value) {
                    MainScreen(navController = navController)
                } else {
                    LoginScreen(navController = navController, onLoginSuccess = {
                        // Update login status after successful login
                        isUserLoggedIn.value = true
                    })
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomAppBar(navController = navController) }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            AppNavigation(navController = navController)
        }
    }
}



@Composable
fun BottomAppBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiary,
    ) {
        val navigationitems = listOf(
            Navigationitem(
                title = stringResource(id = R.string.matkul),
                icon = painterResource(id = R.drawable.baseline_book_24),
                screen = Screen.Matkul
            ),
            Navigationitem(
                title = stringResource(id = R.string.tugas),
                icon = painterResource(id = R.drawable.baseline_assignment_24),
                screen = Screen.Tugas
            ),
            Navigationitem(
                title = stringResource(id = R.string.profile),
                icon = painterResource(id = R.drawable.baseline_account_circle_24),
                screen = Screen.Profile
            )
        )

        navigationitems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = item.icon,
                        contentDescription = item.title,
                        tint = LightPurple
                    )
                },
                label = { Text(item.title) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.startDestinationRoute!!) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Matkul.route) {
        composable(Screen.Matkul.route) { MatkulScreen() }
        composable(Screen.Tugas.route) { TugasScreen() }
        composable(Screen.Profile.route) { ProfileScreen() }
    }
}