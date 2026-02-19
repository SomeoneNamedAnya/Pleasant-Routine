package com.coursework.pleasantroutineui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.pages.NotePage
import com.coursework.pleasantroutineui.pages.NotePageViewModel
import com.coursework.pleasantroutineui.pages.profile.AccountInfoScreen
import com.coursework.pleasantroutineui.pages.profile.AccountInfoViewModel
import com.coursework.pleasantroutineui.pages.profile.AllUserInfoViewModel
import com.coursework.pleasantroutineui.pages.entrance.RegistrationScreen
import com.coursework.pleasantroutineui.pages.room.MainRoomPageViewModel
import com.coursework.pleasantroutineui.pages.room.MainRoomScreen
import com.coursework.pleasantroutineui.pages.room.PersonalArchivePage
import com.coursework.pleasantroutineui.pages.room.PersonalArchivePageViewModel
import com.coursework.pleasantroutineui.repo.TestAccountRepo
import com.coursework.pleasantroutineui.repo.TestRoomRepo
import com.coursework.pleasantroutineui.repo.TestNoteRepo
import com.coursework.pleasantroutineui.ui_services.NotePageViewModelFactory


@Composable
fun App() {
    val navController = rememberNavController()
    val repo = TestAccountRepo()
    val repoRoom = TestRoomRepo()
    val repoNotes = TestNoteRepo()

    val factory = AccountInfoViewModelFactory(repo)
    val factoryRoom = RoomViewModelFactory(repoRoom)
    val factoryAllAccount = AllAccountInfoViewModelFactory(repo)
    val factoryNotePage = NotePageViewModelFactory(repoNotes)
    val factoryPersonalArchivePage = PersonalArchivePageViewModelFactory(repoNotes)

    val vm: AccountInfoViewModel = viewModel(factory = factory)
    val vmRoom: MainRoomPageViewModel = viewModel(factory = factoryRoom)
    val vmAllUser: AllUserInfoViewModel = viewModel(factory = factoryAllAccount)
    val vmNote: NotePageViewModel = viewModel(factory = factoryNotePage)
    val vmPersonalArchivePage: PersonalArchivePageViewModel = viewModel(factory = factoryPersonalArchivePage)

    NavHost(
        navController = navController,
        startDestination = Destinations.PERSONAL_ARCHIVE_PAGE.title + "/0"
    ) {

        composable(Destinations.USER_ACCOUNT_PAGE.title) { AccountInfoScreen(navController, vm) }
        composable(Destinations.LOGIN_PAGE.title) { RegistrationScreen(navController)}
        composable(Destinations.USER_ROOM_PAGE.title) { MainRoomScreen(navController, vmRoom)}
        composable(
            route = Destinations.ALL_USER_INFO_PAGE.title + "/{id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("id") ?: ""
            AccountInfoScreen(
                id = userId,
                navController = navController,
                vm = vmAllUser
            )
        }
        composable(
            route = Destinations.NOTE_PAGE.title + "/{id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("id") ?: ""
            NotePage(
                id = userId,
                navController = navController,
                vm = vmNote
            )
        }

        composable(Destinations.PUBLIC_ROOM_PAGE.title) { RegistrationScreen(navController)}
        composable(Destinations.TASK_PAGE.title) { RegistrationScreen(navController)}
        composable(Destinations.ROOM_CHAT_PAGE.title) { RegistrationScreen(navController)}
        composable(Destinations.COMMON_ARCHIVE_PAGE.title) { RegistrationScreen(navController)}
        composable(
            route = Destinations.PERSONAL_ARCHIVE_PAGE.title + "/{id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("id") ?: ""
            PersonalArchivePage(userId, navController, vmPersonalArchivePage)
        }
    }
}