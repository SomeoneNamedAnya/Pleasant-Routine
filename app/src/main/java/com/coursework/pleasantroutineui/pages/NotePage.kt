package com.coursework.pleasantroutineui.pages

import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


import RoomUserPreview
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.repo.interfaces.IRoomRepo
import com.coursework.pleasantroutineui.ui_services.DrawerContent
import com.coursework.pleasantroutineui.ui_services.Menue
import com.coursework.pleasantroutineui.ui_services.ProfileTopBar
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ChipDefaults.primaryChipColors
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.coursework.pleasantroutineui.R
import com.coursework.pleasantroutineui.domain.Button
import com.coursework.pleasantroutineui.domain.Destinations

class NotePageViewModel (
    private val repository: INotesRepo
) : ViewModel() {
    private val noteId = MutableLiveData<String>()
    var getNote = noteId.switchMap { id ->
        liveData {
            emit(repository.getNote(id))
        }
    }
    fun loadNoteId(id: String) {
        noteId.value = id
    }



}


@Composable
fun NotePage(id: String, navController: NavController, vm: NotePageViewModel) {

    val note by vm.getNote.observeAsState()
    vm.loadNoteId(id)

    Menue("Заметка", false, navController) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()

        ) {


            Text(
                text = note!!.title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Start).padding(5.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Дата создания:" + note?.createTime,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.Start).padding(5.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Дата редактирования:" + note?.lastEditTime,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.Start).padding(5.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            ExpandableContainer(
                collapsedContent = {
                    FlowRow {
                        note!!.tags.take(4).forEach {
                            Chip(
                                colors = primaryChipColors(
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                    backgroundColor = MaterialTheme.colorScheme.surface,
                                    secondaryContentColor = MaterialTheme.colorScheme.onSurface,
                                    iconColor = MaterialTheme.colorScheme.onSurface,
                                ),
                                onClick = {}, label = { Text(it, color = MaterialTheme.colorScheme.onSurface) })
                        }
                    }
                },
                expandedContent = {
                    FlowRow {
                        note!!.tags.forEach {
                            Chip(
                                colors = primaryChipColors(
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                    backgroundColor = MaterialTheme.colorScheme.surface,
                                    secondaryContentColor = MaterialTheme.colorScheme.onSurface,
                                    iconColor = MaterialTheme.colorScheme.onSurface,
                                ),
                                onClick = {}, label = { Text(it, color = MaterialTheme.colorScheme.onSurface) })
                        }
                    }
                }
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow{

                items(count = note!!.photoLinks.size) { ind ->
                    Spacer(modifier = Modifier.width(5.dp))
                    SmallPhoto(note!!.photoLinks[ind], 100.dp)
                    if (ind + 1 == note!!.photoLinks.size) {
                        Spacer(modifier = Modifier.width(5.dp))
                    } else {
                        Spacer(modifier = Modifier.width(15.dp))
                    }

                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = note!!.text,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 5.dp, end = 5.dp)
            )


        }
    }
}

@Composable
fun SmallPhoto(link: String, size: Dp) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(link)
            .placeholder(R.drawable.loading)
            .error(R.drawable.no_photo)
            .build(),
        contentDescription = "Example Image",
        modifier = Modifier.size(size),
        contentScale = ContentScale.Crop,
        onError = {
            Log.e(
                "AsyncImage",
                "Failed to load image: ${it.result.throwable}"
            )
        }
    )
}


@Composable
fun ExpandableContainer(
    collapsedContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier.padding(start = 5.dp, end = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .animateContentSize()
        ) {
            if (expanded) {
                expandedContent()
            } else {
                collapsedContent()
            }
        }

        TextButton(
            onClick = { expanded = !expanded },
            contentPadding = PaddingValues(5.dp),

        ) {
            Text(if (expanded) "Свернуть" else "Показать ещё")
        }
    }
}