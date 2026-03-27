package com.example.contacts.contact

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ContactsViewModel : ViewModel() {

    private val _contacts = MutableStateFlow(
        listOf(
            Contact(UUID.randomUUID().toString(), "Ana", "García", "ana.garcia@correo.com", "+34 612 345 678"),
            Contact(UUID.randomUUID().toString(), "Carlos", "López", "carlos.lopez@correo.com", "+34 698 765 432"),
            Contact(UUID.randomUUID().toString(), "María", "Martínez", "maria.martinez@correo.com", "+34 655 111 222"),
            Contact(UUID.randomUUID().toString(), "Luis", "Fernández", "luis.fernandez@correo.com", "+34 677 888 999"),
        )
    )
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    fun addContact(name: String, lastName: String, email: String, phoneNumber: String) {
        _contacts.value = _contacts.value + Contact(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            lastName = lastName.trim(),
            email = email.trim(),
            phoneNumber = phoneNumber.trim()
        )
    }

    fun updateContact(contact: Contact) {
        _contacts.value = _contacts.value.map { if (it.id == contact.id) contact else it }
    }

    fun deleteContact(contact: Contact) {
        _contacts.value = _contacts.value.filter { it.id != contact.id }
    }

    fun getContactById(id: String): Contact? = _contacts.value.find { it.id == id }
}
