package com.example.contacts.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import com.example.contacts.contact.Contact
import com.example.contacts.contact.ContactsViewModel
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    viewModel: ContactsViewModel,
    onAddContact: () -> Unit,
    onEditContact: (String) -> Unit
) {
    val contacts by viewModel.contacts.collectAsState()
    var contactToDelete by remember { mutableStateOf<Contact?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val filteredContacts = remember(contacts, searchQuery) {
        if (searchQuery.isBlank()) contacts
        else contacts.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.lastName.contains(searchQuery, ignoreCase = true) ||
                    it.phoneNumber.contains(searchQuery, ignoreCase = true) ||
                    it.email.contains(searchQuery, ignoreCase = true)
        }
    }

    contactToDelete?.let { contact ->
        AlertDialog(
            onDismissRequest = { contactToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Eliminar contacto") },
            text = {
                Text(
                    "¿Estás seguro de que quieres eliminar a " +
                            "${contact.name} ${contact.lastName}? Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteContact(contact)
                        contactToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { contactToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Contactos") },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddContact,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nuevo contacto") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar contactos...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = MaterialTheme.shapes.large,
                singleLine = true
            )

            if (filteredContacts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Text(
                            text = if (searchQuery.isBlank()) "Sin contactos aún" else "Sin resultados",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (searchQuery.isBlank()) {
                            Text(
                                text = "Pulsa + para agregar tu primer contacto",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(
                        items = filteredContacts,
                        key = { it.id }
                    ) { contact ->
                        SwipeableContactItem(
                            contact = contact,
                            onEdit = { onEditContact(contact.id) },
                            onDelete = { contactToDelete = contact }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeableContactItem(
    contact: Contact,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var maxRevealPx by remember(contact.id) { mutableFloatStateOf(0f) }
    var offsetPx by remember(contact.id) { mutableFloatStateOf(0f) }
    var itemHeightPx by remember(contact.id) { mutableFloatStateOf(0f) }
    val itemHeight = with(LocalDensity.current) {
        if (itemHeightPx > 0f) itemHeightPx.toDp() else Dp.Unspecified
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        SwipeActions(
            modifier = Modifier
                .fillMaxSize()
                .then(if (itemHeightPx > 0f) Modifier.height(itemHeight) else Modifier),
            onWidthMeasured = { maxRevealPx = it },
            onEdit = {
                offsetPx = 0f
                onEdit()
            },
            onDelete = {
                offsetPx = 0f
                onDelete()
            }
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetPx.roundToInt(), 0) }
                .onSizeChanged { itemHeightPx = it.height.toFloat() }
                .pointerInput(maxRevealPx) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            if (maxRevealPx <= 0f) return@detectHorizontalDragGestures
                            change.consume()
                            offsetPx = (offsetPx + dragAmount).coerceIn(-maxRevealPx, 0f)
                        },
                        onDragEnd = {
                            if (maxRevealPx <= 0f) return@detectHorizontalDragGestures
                            val openThresholdPx = maxRevealPx * 0.08f
                            val keepOpen = offsetPx <= -openThresholdPx
                            offsetPx = if (keepOpen) -maxRevealPx else 0f
                        },
                        onDragCancel = {
                            val openThresholdPx = maxRevealPx * 0.08f
                            if (maxRevealPx > 0f && offsetPx <= -openThresholdPx) {
                                offsetPx = -maxRevealPx
                            } else {
                                offsetPx = 0f
                            }
                        }
                    )
                }
        ) {
            ContactCard(contact = contact)
        }
    }
}

@Composable
fun SwipeActions(
    modifier: Modifier = Modifier,
    onWidthMeasured: (Float) -> Unit = {},
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .onSizeChanged { onWidthMeasured(it.width.toFloat()) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier
                    .width(104.dp)
                    .fillMaxHeight(),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.height(6.dp))
                    Text("Editar")
                }
            }
            Button(
                onClick = onDelete,
                modifier = Modifier
                    .width(104.dp)
                    .fillMaxHeight(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.height(6.dp))
                    Text("Eliminar")
                }
            }
        }
    }
}

private val avatarColors = listOf(
    Color(0xFF6750A4), Color(0xFF006E1C), Color(0xFF9A4521),
    Color(0xFF006A6A), Color(0xFF005FAF), Color(0xFF8B4000),
    Color(0xFF9A0007), Color(0xFF6B5778)
)

@Composable
fun ContactCard(contact: Contact) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(name = contact.name, lastName = contact.lastName)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${contact.name} ${contact.lastName}".trim(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (contact.phoneNumber.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = contact.phoneNumber,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (contact.email.isNotBlank()) {
                    Text(
                        text = contact.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ContactAvatar(name: String, lastName: String) {
    val initials = buildString {
        if (name.isNotEmpty()) append(name.first().uppercaseChar())
        if (lastName.isNotEmpty()) append(lastName.first().uppercaseChar())
    }
    val avatarColor = avatarColors[abs(name.hashCode()) % avatarColors.size]

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(avatarColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}
