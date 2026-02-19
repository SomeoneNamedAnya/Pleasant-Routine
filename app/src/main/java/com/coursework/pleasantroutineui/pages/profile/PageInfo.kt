package com.coursework.pleasantroutineui.pages.profile

import android.content.ClipData
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.coursework.pleasantroutineui.ui_services.DrawerContent
import com.coursework.pleasantroutineui.ui_services.ProfileTopBar
import com.coursework.pleasantroutineui.R
import com.coursework.pleasantroutineui.repo.interfaces.IAccountRepo
import com.coursework.pleasantroutineui.ui_services.InfoRow
import com.coursework.pleasantroutineui.ui_services.Menue

import kotlinx.coroutines.launch


class AccountInfoViewModel (
    private val repository: IAccountRepo
): ViewModel() {

    var user = liveData {
        emit(repository.getUser("0"))
    }


}

@Composable
fun AccountInfoScreen(navController: NavController, vm: AccountInfoViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                    .padding(top = 50.dp, bottom = 20.dp)
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                val user by vm.user.observeAsState()
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
                    derivedStateOf { user?.selfInfo}
                }


                val clipboard = LocalClipboard.current
                val scope = rememberCoroutineScope()
                Spacer(modifier = Modifier.height(15.dp))
                AsyncImage(
                    model = photoLink,
                    onError = {
                        Log.e(
                            "AsyncImage",
                            "Failed to load image: ${it.result.throwable}"
                        )
                    },
                    contentDescription = "Example Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(id = R.drawable.no_photo), // Replace with your placeholder drawable
                    error = painterResource(id = R.drawable.no_photo)  // Replace with your error drawable
                )

                Spacer(modifier = Modifier.height(15.dp))
                userId?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleSmall
                        )
                        IconButton(
                            onClick = {
                                scope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText(
                                                it,
                                                it
                                            )
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


