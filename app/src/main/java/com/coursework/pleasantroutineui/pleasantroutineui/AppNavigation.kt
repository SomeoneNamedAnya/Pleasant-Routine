package com.coursework.pleasantroutineui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.pages.common_services.DiscoveryPage
import com.coursework.pleasantroutineui.pages.common_services.DiscoveryViewModel
import com.coursework.pleasantroutineui.pages.entrance.ChangePasswordScreen
import com.coursework.pleasantroutineui.pages.entrance.ChangePasswordViewModel
import com.coursework.pleasantroutineui.pages.entrance.FastRoute
import com.coursework.pleasantroutineui.pages.entrance.FastViewModel
import com.coursework.pleasantroutineui.pages.entrance.LogoutRoute
import com.coursework.pleasantroutineui.pages.entrance.LogoutViewModel
import com.coursework.pleasantroutineui.pages.profile.AccountInfoScreen
import com.coursework.pleasantroutineui.pages.profile.AllUserInfoViewModel
import com.coursework.pleasantroutineui.pages.entrance.RegistrationScreen
import com.coursework.pleasantroutineui.pages.entrance.RegistrationViewModel
import com.coursework.pleasantroutineui.pages.entrance.VoluntaryChangePasswordScreen
import com.coursework.pleasantroutineui.pages.entrance.VoluntaryChangePasswordViewModel
import com.coursework.pleasantroutineui.pages.profile.AccountInfoViewModel
import com.coursework.pleasantroutineui.pages.room.ArchivePage
import com.coursework.pleasantroutineui.pages.room.ArchivePageViewModel
import com.coursework.pleasantroutineui.pages.room.ChatPage
import com.coursework.pleasantroutineui.pages.room.ChatViewModel
import com.coursework.pleasantroutineui.pages.room.CreateTaskScreen
import com.coursework.pleasantroutineui.pages.room.CreateTaskViewModel
import com.coursework.pleasantroutineui.pages.room.KanbanScreen
import com.coursework.pleasantroutineui.pages.room.KanbanViewModel
import com.coursework.pleasantroutineui.pages.room.MainRoomPageViewModel
import com.coursework.pleasantroutineui.pages.room.MainRoomScreen
import com.coursework.pleasantroutineui.pages.room.PersonalArchivePage
import com.coursework.pleasantroutineui.pages.room.PersonalArchivePageViewModel
import com.coursework.pleasantroutineui.pages.room.PublicRoomPage
import com.coursework.pleasantroutineui.pages.room.PublicRoomViewModel
import com.coursework.pleasantroutineui.pages.room.TaskPageViewModel
import com.coursework.pleasantroutineui.pages.room.TaskScreen
import com.coursework.pleasantroutineui.pages.NotePage
import com.coursework.pleasantroutineui.pages.NotePageViewModel


@SuppressLint("SuspiciousIndentation")
@Composable
fun App() {


    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.FAST_PAGE.title,
    ) {
        composable(Destinations.FAST_PAGE.title) {
            val vm: FastViewModel = hiltViewModel()
            FastRoute(vm.state, navController)
        }
        composable(Destinations.LOGOUT_PAGE.title) {
            val vm: LogoutViewModel = hiltViewModel()
            LogoutRoute(vm.state, navController)
        }
        composable(Destinations.USER_ACCOUNT_PAGE.title) {
            val curVm: AllUserInfoViewModel = hiltViewModel()
            AccountInfoScreen(navController, curVm)
        }
        composable(Destinations.LOGIN_PAGE.title) {
            val curVm: RegistrationViewModel = hiltViewModel()
            RegistrationScreen(navController, curVm)
        }
        composable(Destinations.USER_ROOM_PAGE.title) {
            val curVm: MainRoomPageViewModel = hiltViewModel()
            MainRoomScreen(navController, curVm)
        }
        composable(Destinations.ALL_USER_INFO_PAGE.title + "/{userId}") {
            arguments ->
                val userId = arguments.arguments?.getString("userId")?.toString()

                val curVm: AccountInfoViewModel = hiltViewModel()
                AccountInfoScreen(userId, navController, curVm)
        }

        composable(Destinations.PERSONAL_ARCHIVE_PAGE.title) {
                arguments ->
            val curVm: PersonalArchivePageViewModel = hiltViewModel()
            PersonalArchivePage(navController, curVm)
        }
        composable(Destinations.COMMON_ARCHIVE_PAGE.title) {
                arguments ->
            val curVm: ArchivePageViewModel = hiltViewModel()
            ArchivePage(navController, curVm)
        }
        composable(Destinations.PUBLIC_ROOM_PAGE.title) {
                arguments ->
            val curVm: PublicRoomViewModel = hiltViewModel()
            PublicRoomPage("1", navController, curVm)
        }
        composable(Destinations.DESK_PAGE.title) {
                arguments ->
            val curVm: KanbanViewModel = hiltViewModel()
            KanbanScreen(navController, curVm)
        }
        composable(Destinations.DESK_PAGE.title) {
                arguments ->
            val curVm: KanbanViewModel = hiltViewModel()
            KanbanScreen(navController, curVm)
        }
        composable(Destinations.TASK_PAGE.title + "/{taskId}") {
                arguments ->
            val taskId = arguments.arguments?.getString("taskId")?.toLong() ?: 0
            println(taskId)
            val curVm: TaskPageViewModel = hiltViewModel()
            TaskScreen(taskId, navController, curVm)
        }

        composable(Destinations.CREATE_TASK.title + "/{roomId}") {
                arguments ->
            val vm: CreateTaskViewModel = hiltViewModel()
            val roomId = arguments.arguments?.getString("roomId")?.toLong() ?: 0

            CreateTaskScreen(
                roomId = roomId,
                navController = navController,
                vm = vm
            )
        }

        composable(Destinations.ROOM_CHAT_PAGE.title + "/{userId}") {
                arguments ->
            val vm: ChatViewModel = hiltViewModel()
            val userId = arguments.arguments?.getString("userId")?.toLong() ?: 0

            ChatPage(
                userId = userId,
                navController = navController,
                vm
            )
        }

        composable(Destinations.DISCOVERY_PAGE.title) {
            val vm: DiscoveryViewModel = hiltViewModel()
            DiscoveryPage(navController = navController, vm = vm)
        }
        composable(
            route = "${Destinations.CHANGE_PASSWORD_PAGE.title}/{email}/{oldPassword}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("oldPassword") { type = NavType.StringType }
            )
        ) {
            val vm: ChangePasswordViewModel = hiltViewModel()
            ChangePasswordScreen(navController = navController, vm = vm)
        }

        composable(Destinations.VOLUNTARY_CHANGE_PASSWORD_PAGE.title) {
            val vm: VoluntaryChangePasswordViewModel = hiltViewModel()
            VoluntaryChangePasswordScreen(navController = navController, vm = vm)
        }

        composable(
            route = "${Destinations.NOTE_PAGE.title}/{noteRef}",
            arguments = listOf(navArgument("noteRef") { type = NavType.StringType })
        ) { backStackEntry ->
            val noteRef = backStackEntry.arguments?.getString("noteRef") ?: "p_new"
            val vm: NotePageViewModel = hiltViewModel()
            NotePage(noteRef = noteRef, navController = navController, vm = vm)
        }

    }
}