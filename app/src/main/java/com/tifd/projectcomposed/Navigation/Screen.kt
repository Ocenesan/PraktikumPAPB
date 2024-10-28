package com.tifd.projectcomposed.Navigation

sealed class Screen(val route: String){
    object Matkul : Screen("Mata Kuliah")
    object Tugas : Screen("Tugas")
    object Profil : Screen ("Profil")
}
