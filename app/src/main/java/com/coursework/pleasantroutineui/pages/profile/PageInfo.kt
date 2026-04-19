package com.coursework.pleasantroutineui.pages.profile

import android.content.ClipData
import android.net.Uri
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.coursework.pleasantroutineui.R
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.repo.interfaces.IAccountRepo
import com.coursework.pleasantroutineui.repo.prod.IUserRepo
import com.coursework.pleasantroutineui.ui_services.InfoRow
import com.coursework.pleasantroutineui.ui_services.Menue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountInfoViewModel @Inject constructor(
    private val repository: IUserRepo
): ViewModel(){

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun loadUser(id: String) {
        viewModelScope.launch {
            _user.value = repository.getUser(id)
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

@Composable
fun AccountInfoScreen(id: String?, navController: NavController, vm: AccountInfoViewModel) {

    LaunchedEffect(Unit) {
        if (id != null) {
            vm.loadUser(id)
        }
    }
    val user by vm.user.collectAsState()

    Menue("Информация", false, navController) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                val photoLink by remember(user) {
                    derivedStateOf { user?.photoLink }
                }
                val userId by remember(user) {
                    derivedStateOf { user?.id}
                }
                val userFullName by remember(user) {
                    derivedStateOf { user?.firstName + " " + user?.surname + " " + user?.lastName}
                }
                val userDateOfBirth by remember(user) {
                    derivedStateOf { user?.dateOfBirth}
                }
                val userEmail by remember(user) {
                    derivedStateOf { user?.email}
                }
                val userRoomNumber by remember(user) {
                    derivedStateOf { user?.roomNumber}
                }
                val userDepartment by remember(user) {
                    derivedStateOf { user?.department}
                }
                val userEducationalProgram by remember(user) {
                    derivedStateOf { user?.educationalProgram}
                }
                val userEducationLevel by remember(user) {
                    derivedStateOf { user?.educationLevel}
                }
                val userSelfInfo by remember(user) {
                    derivedStateOf { user?.about}
                }


                val clipboard = LocalClipboard.current
                val scope = rememberCoroutineScope()
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

                userFullName?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 15.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                InfoRow("О себе:", "")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp, start = 20.dp, end = 15.dp)
                ) {

                    Text(
                        text = userSelfInfo ?: "—",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 15.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                InfoRow("Дата рождения:", userDateOfBirth ?: "Не указано")
                InfoRow("Учебная почта:", userEmail ?: "Не указано")
                InfoRow("Номер комнаты:", userRoomNumber ?: "Не указано")
                InfoRow("Факультет:", userDepartment ?: "Не указано")
                InfoRow("Образовательная программа:", userEducationalProgram ?: "Не указано")
                InfoRow("Уровень обучения:", userEducationLevel ?: "Не указано")


            }
        }
    }


}


