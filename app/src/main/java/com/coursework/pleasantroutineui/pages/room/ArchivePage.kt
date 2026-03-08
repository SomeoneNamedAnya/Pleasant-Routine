package com.coursework.pleasantroutineui.pages.room

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo
import com.coursework.pleasantroutineui.ui_services.Menue
import java.time.LocalDate
import java.time.ZoneId


class ArchivePageViewModel (
    private val repository: INotesRepo
) : ViewModel() {

    private val accountId = MutableLiveData<String>()
    var allNotes = accountId.switchMap { id ->
        liveData {
            emit(repository.getAllNotes(id))
        }
    }
    var allTags = accountId.switchMap { id ->
        liveData {
            emit(repository.getAllTags(id))
        }
    }

    var allUsers = accountId.switchMap { id ->
        liveData {
            emit(repository.getAllUsers(id))
        }
    }


    fun loadAccountId(id: String) {
        accountId.value = id
    }


}


@Composable
fun ArchivePage(id: String, navController: NavController, vm: ArchivePageViewModel) {

    vm.loadAccountId(id)
    val allNotes by vm.allNotes.observeAsState()
    val allTags by vm.allTags.observeAsState()
    val allUser by vm.allUsers.observeAsState()

    val allUserString = allUser?.map{it.firstName + " " + it.surname}?.toTypedArray()


    Menue("Общий архив", false, navController) { paddingValues ->
        var selectedDateRange by remember {
            mutableStateOf<Pair<Long?, Long?>?>(null)
        }

        var showModal by remember {
            mutableStateOf(false)
        }
        var selectedItems by remember { mutableStateOf(setOf<String>()) }
        var selectedUsers by remember { mutableStateOf(setOf<String>()) }

        var startDate by remember { mutableStateOf<Long?>(null) }
        var endDate by remember { mutableStateOf<Long?>(null) }
        Column (modifier = Modifier.padding(paddingValues)) {
            Spacer(modifier = Modifier.height(10.dp))

            ComplexFilter(
                allTags,
                allUserString,
                selectedUsers,
                {newSet ->
                    selectedUsers = newSet
                },
                selectedItems,
                onSelectedItemsUpdate = { newSet ->
                    selectedItems = newSet
                },
                startDate,
                onStartDateUpdate = {
                        newStartDate ->
                    startDate = newStartDate
                },
                endDate,
                onEndDateUpdate = {
                        newEndDate ->
                    endDate = newEndDate
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn{


                if (allNotes != null) {
                    items(allNotes!!.size) { item ->
                        var flagTag = false
                        if (selectedItems.isEmpty()) {
                            flagTag = true
                        } else {

                            for (tag in allNotes!![item].tags) {
                                if (selectedItems.contains(tag)) {
                                    flagTag = true
                                    break
                                }
                            }



                        }

                        var userFlag = false
                        if (selectedUsers.isEmpty()) {
                            userFlag = true
                        }
                        for (user in allNotes!![item].owner) {
                            println(selectedUsers)
                            if (selectedUsers.contains("${user.firstName} ${user.surname}")) {
                                userFlag = true
                                break
                            }
                        }

                        var flagDate = false
                        val noteDate =  LocalDate
                            .parse(allNotes!![item].createTime)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()
                        if (startDate != null && endDate != null) {
                            if (noteDate >= startDate!! && noteDate <= endDate!!) {
                                flagDate = true
                            }
                        } else if (startDate != null) {
                            if (noteDate >= startDate!!) {
                                flagDate = true
                            }
                        } else if (endDate != null) {
                            if (noteDate <= endDate!!) {
                                flagDate = true
                            }
                        } else {
                            flagDate = true
                        }
                        if (flagTag && flagDate && userFlag) {
                            NotePreview(allNotes!![item], 250.dp, navController)
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                    }
                }

            }
        }
    }
}
