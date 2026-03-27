package com.example.contacts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.contacts.contact.Contact
import com.example.contacts.contact.ContactsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactFormScreen(
    viewModel: ContactsViewModel,
    contactId: String?,
    onNavigateBack: () -> Unit
) {
    val isEditing = contactId != null
    val existingContact: Contact? = remember(contactId) {
        contactId?.let { viewModel.getContactById(it) }
    }

    var name by remember { mutableStateOf(existingContact?.name ?: "") }
    var lastName by remember { mutableStateOf(existingContact?.lastName ?: "") }
    var email by remember { mutableStateOf(existingContact?.email ?: "") }
    var phoneNumber by remember { mutableStateOf(existingContact?.phoneNumber ?: "") }
    var nameError by remember { mutableStateOf(false) }

    fun save() {
        if (name.isBlank()) {
            nameError = true
            return
        }
        if (isEditing && existingContact != null) {
            viewModel.updateContact(
                existingContact.copy(
                    name = name.trim(),
                    lastName = lastName.trim(),
                    email = email.trim(),
                    phoneNumber = phoneNumber.trim()
                )
            )
        } else {
            viewModel.addContact(
                name = name.trim(),
                lastName = lastName.trim(),
                email = email.trim(),
                phoneNumber = phoneNumber.trim()
            )
        }
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar contacto" else "Nuevo contacto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = ::save,
                        enabled = name.isNotBlank()
                    ) {
                        Text(
                            "Guardar",
                            color = if (name.isNotBlank())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = { Text("Nombre *") },
                isError = nameError,
                supportingText = if (nameError) {
                    { Text("El nombre es obligatorio") }
                } else null,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
