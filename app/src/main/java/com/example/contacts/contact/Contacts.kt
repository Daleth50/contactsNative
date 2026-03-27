package com.example.contacts.contact

class Contacts {
    private val contacts: MutableList<Contact> = mutableListOf()

    fun addContact(contact: Contact) {
        contacts.add(contact)
    }

    fun removeContact(contact: Contact) {
        contacts.remove(contact)
    }

    fun updateContact(contact: Contact) {
        val index = contacts.indexOf(contact)
        if (index != -1) {
            contacts[index] = contact
        }
    }

    fun getContacts(): List<Contact> {
        return contacts
    }

    fun getContactById(id: String): Contact? {
        return contacts.find { it.id == id }
    }

    fun searchContacts(query: String): List<Contact> {
        return contacts.filter {
            it.name.contains(query, ignoreCase = true)
                    || it.phoneNumber.contains(query, ignoreCase = true)
                    || it.email.contains(query, ignoreCase = true)
        }
    }
}