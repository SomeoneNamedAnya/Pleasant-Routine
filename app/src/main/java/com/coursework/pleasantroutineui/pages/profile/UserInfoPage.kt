package com.coursework.pleasantroutineui.pages.profile

import android.content.ClipData
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.coursework.pleasantroutineui.R
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.repo.prod.IUserRepo
import com.coursework.pleasantroutineui.ui_services.InfoRow
import com.coursework.pleasantroutineui.ui_services.Menue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllUserInfoViewModel @Inject constructor(
    private val repository: IUserRepo
): ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun loadUser() {
        viewModelScope.launch {
            _user.value = repository.getSelfInfo()
        }
    }

    fun updateAbout(about: String) {

        viewModelScope.launch {
            repository.setAbout(about)
            println(_user.value)
            _user.value = _user.value?.copy(about = about)
        }
    }

    fun updatePhoto(photo: Uri) {

        viewModelScope.launch {
            val link: String = repository.setPhoto(photo)
            _user.value = _user.value?.copy(photoLink = link,
                signedLink = null)
            print("NEW LINK $link")
        }
    }

    fun refreshSignedUrl(link: String) {
        viewModelScope.launch {
            if (link != "") {

                val linkSign: String = repository.signedLink(link)
                _user.value = _user.value?.copy(signedLink = linkSign)
            }

        }
    }
}
public fun buildImageCacheKey(userId: String?, photoLink: String?): String {
    return "avatar_${userId}_${photoLink?.substringBeforeLast('?') ?: ""}"
}
@Composable
fun AccountInfoScreen(
    navController: NavController,
    vm: AllUserInfoViewModel,
) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        vm.loadUser()
    }

    val user by vm.user.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    LaunchedEffect(user) {
        text = user?.about ?: ""
    }
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            vm.updatePhoto(it)
        }
    }

    Menue("Профиль", true, navController) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user?.signedLink ?: "")
                            .diskCacheKey(buildImageCacheKey(user?.id, user?.photoLink))
                            .memoryCacheKey(buildImageCacheKey(user?.id, user?.photoLink))
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .networkCachePolicy(CachePolicy.ENABLED)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize(),
                        error = painterResource(R.drawable.no_photo),
                        placeholder = painterResource(R.drawable.no_photo),
                        onError = {
                            vm.refreshSignedUrl(user?.photoLink ?: "")
                        }
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.7f))
                    )

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = 60.dp)
                    ) {
                        println("photo_link")
                        println(user?.photoLink)
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user?.signedLink ?: "")
                                .diskCacheKey(buildImageCacheKey(user?.id, user?.photoLink))
                                .memoryCacheKey(buildImageCacheKey(user?.id, user?.photoLink))
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .networkCachePolicy(CachePolicy.ENABLED)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .matchParentSize()
                                .clip(CircleShape),
                            error = painterResource(R.drawable.no_photo),
                            placeholder = painterResource(R.drawable.no_photo),
                            onError = {
                                println("WHY?????????")
                                vm.refreshSignedUrl(user?.photoLink ?: "")
                            }
                        )

                    }
                    IconButton(
                        onClick = {
                            picker.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(44.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.sharp_add_a_photo_24),
                            contentDescription = "Изменить фото",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }


                }

                Spacer(modifier = Modifier.height(50.dp))

                user?.id?.let { id ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = id,
                                style = MaterialTheme.typography.titleSmall
                            )


                            IconButton(
                                onClick = {
                                    scope.launch {
                                        clipboard.setClipEntry(
                                            ClipEntry(
                                                ClipData.newPlainText(id, id)
                                            )
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_content_copy_24),
                                    contentDescription = "Скопировать",
                                    modifier = Modifier.size(20.dp)
                                )
                            }


                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = listOfNotNull(
                        user?.firstName,
                        user?.surname,
                        user?.lastName
                    ).joinToString(" "),
                    style = MaterialTheme.typography.titleMedium
                )

                HorizontalDivider(
                    modifier = Modifier.padding(15.dp),
                    thickness = 1.dp
                )
                InfoRow(
                    "О себе:",
                    "",
                    isExpand = true,
                    isEdit = isEditing,
                    onEditClick = {
                        if (isEditing) {
                            vm.updateAbout(text)
                        }
                        isEditing = !isEditing
                    }
                )

                if (isEditing) {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 15.dp, end = 15.dp, top = 10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,

                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,

                            cursorColor = MaterialTheme.colorScheme.onBackground,

                            focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground,

                            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                } else {
                    Text(
                        text = user?.about ?: "—",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 15.dp, end = 15.dp, top = 10.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(15.dp),
                    thickness = 1.dp
                )

                InfoRow("Дата рождения:", user?.dateOfBirth ?: "Не указано")
                InfoRow("Учебная почта:", user?.email ?: "Не указано")
                InfoRow("Номер комнаты:", user?.roomNumber ?: "Не указано")
                InfoRow("Факультет:", user?.department ?: "Не указано")
                InfoRow("Образовательная программа:", user?.educationalProgram ?: "Не указано")
                InfoRow("Уровень обучения:", user?.educationLevel ?: "Не указано")
            }
        }
    }
}


