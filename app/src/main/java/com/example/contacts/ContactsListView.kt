package com.example.contacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.contacts.contact.ContactsViewModel
import com.example.contacts.ui.ContactDetailScreen
import com.example.contacts.ui.ContactFormScreen
import com.example.contacts.ui.ContactListScreen
import com.example.contacts.ui.theme.ContactsTheme

class ContactsListView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContactsTheme {
                ContactsListRoute()
            }
        }
    }
}

@Composable
fun ContactsListRoute(contactsViewModel: ContactsViewModel = viewModel()) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            ContactListScreen(
                viewModel = contactsViewModel,
                onAddContact = { navController.navigate("form/new") },
                onContactClick = { id -> navController.navigate("detail/$id") }
            )
        }
        composable(
            route = "detail/{contactId}",
            arguments = listOf(navArgument("contactId") { type = NavType.StringType })
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId")
            if (contactId != null) {
                ContactDetailScreen(
                    viewModel = contactsViewModel,
                    contactId = contactId,
                    onNavigateBack = { navController.popBackStack() },
                    onEditContact = { id -> navController.navigate("form/$id") }
                )
            }
        }
        composable(
            route = "form/{contactId}",
            arguments = listOf(navArgument("contactId") { type = NavType.StringType })
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId")
            ContactFormScreen(
                viewModel = contactsViewModel,
                contactId = if (contactId == "new") null else contactId,
                onNavigateBack = { navController.popBackStack() },
                onDeleteContact = {
                    navController.navigate("list") {
                        popUpTo("list") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}