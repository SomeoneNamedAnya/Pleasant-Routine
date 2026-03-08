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
import com.coursework.pleasantroutineui.pages.room.ArchivePage
import com.coursework.pleasantroutineui.pages.room.ArchivePageViewModel
import com.coursework.pleasantroutineui.pages.room.ChatPage
import com.coursework.pleasantroutineui.pages.room.ChatViewModel
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
import com.coursework.pleasantroutineui.repo.TestAccountRepo
import com.coursework.pleasantroutineui.repo.TestChatRepo
import com.coursework.pleasantroutineui.repo.TestCommonNoteRepo
import com.coursework.pleasantroutineui.repo.TestRoomRepo
import com.coursework.pleasantroutineui.repo.TestNoteRepo
import com.coursework.pleasantroutineui.repo.TestPublicRoomRepo
import com.coursework.pleasantroutineui.repo.TestTaskRepo
import com.coursework.pleasantroutineui.ui_services.NotePageViewModelFactory
import com.coursework.pleasantroutineui.vm_factories.AccountInfoViewModelFactory
import com.coursework.pleasantroutineui.vm_factories.AllAccountInfoViewModelFactory
import com.coursework.pleasantroutineui.vm_factories.ArchivePageViewModelFactory
import com.coursework.pleasantroutineui.vm_factories.ChatPageViewModelFactory
import com.coursework.pleasantroutineui.vm_factories.DeskPageViewModelFactory
import com.coursework.pleasantroutineui.vm_factories.PersonalArchivePageViewModelFactory
import com.coursework.pleasantroutineui.vm_factories.PublicRoomInfoViewModelFactory
import com.coursework.pleasantroutineui.vm_factories.RoomViewModelFactory
import com.coursework.pleasantroutineui.vm_factories.TaskPageViewModelFactory


@Composable
fun App() {
    val navController = rememberNavController()
    val repo = TestAccountRepo()
    val repoRoom = TestRoomRepo()
    val repoNotes = TestNoteRepo()
    val commonNotes = TestCommonNoteRepo()
    val repoTask = TestTaskRepo()
    val repoChat = TestChatRepo()
    val publicRoomInfo = TestPublicRoomRepo()

    val factory = AccountInfoViewModelFactory(repo)
    val factoryRoom = RoomViewModelFactory(repoRoom)
    val factoryAllAccount = AllAccountInfoViewModelFactory(repo)
    val factoryNotePage = NotePageViewModelFactory(repoNotes)
    val factoryPersonalArchivePage = PersonalArchivePageViewModelFactory(repoNotes)
    val factoryArchivePage = ArchivePageViewModelFactory(commonNotes)
    val factoryTask = TaskPageViewModelFactory(repoTask)
    val factoryDesk = DeskPageViewModelFactory(repoTask)
    val factoryChat = ChatPageViewModelFactory(repoChat)
    val factoryPublicRoomRepo = PublicRoomInfoViewModelFactory(publicRoomInfo)

    val vm: AccountInfoViewModel = viewModel(factory = factory)
    val vmRoom: MainRoomPageViewModel = viewModel(factory = factoryRoom)
    val vmAllUser: AllUserInfoViewModel = viewModel(factory = factoryAllAccount)
    val vmNote: NotePageViewModel = viewModel(factory = factoryNotePage)
    val vmPersonalArchivePage: PersonalArchivePageViewModel = viewModel(factory = factoryPersonalArchivePage)
    val vmArchivePage: ArchivePageViewModel = viewModel(factory = factoryArchivePage)
    val vmTaskPage: TaskPageViewModel = viewModel(factory = factoryTask)
    val vmDeskPage: KanbanViewModel = viewModel(factory = factoryDesk)
    val vmChatPage: ChatViewModel = viewModel(factory = factoryChat)
    val vmPublicRoomInfo: PublicRoomViewModel = viewModel(factory = factoryPublicRoomRepo)

    NavHost(
        navController = navController,
        startDestination = Destinations.PUBLIC_ROOM_PAGE.title + "/C081/0",
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
            route = Destinations.ROOM_CHAT_PAGE.title + "/{chat_id}/{user_id}",
            arguments = listOf(
                navArgument("chat_id") {
                    type = NavType.StringType
                },
                navArgument("user_id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("user_id") ?: ""
            val chatId = backStackEntry.arguments?.getString("chat_id") ?: ""
            ChatPage(
                chatId = chatId,
                userId = userId,
                navController = navController,
                viewModel = vmChatPage
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

        composable(
            route = Destinations.PUBLIC_ROOM_PAGE.title + "/{roomId}/{userId}",
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.StringType
                },
                navArgument("userId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            PublicRoomPage(
                roomNumber = roomId,
                currentUserId = userId,
                navController = navController,
                viewModel = vmPublicRoomInfo
            )
        }
        composable(
            route = Destinations.TASK_PAGE.title + "/{id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("id") ?: ""
            TaskScreen(userId, navController, vmTaskPage)
        }
        composable(
            route = Destinations.DESK_PAGE.title + "/{id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("id") ?: ""
            KanbanScreen(userId, navController, vmDeskPage)
        }
        composable(
            route = Destinations.COMMON_ARCHIVE_PAGE.title + "/{id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("id") ?: ""
            ArchivePage(userId, navController, vmArchivePage)
        }

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