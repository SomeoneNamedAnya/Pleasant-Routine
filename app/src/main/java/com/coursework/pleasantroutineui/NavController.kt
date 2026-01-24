package com.coursework.pleasantroutineui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.pages.AccountInfoScreen
import com.coursework.pleasantroutineui.pages.AccountInfoViewModel
import com.coursework.pleasantroutineui.pages.RegistrationScreen
import com.coursework.pleasantroutineui.pages.room_pages.MainRoomPageViewModel
import com.coursework.pleasantroutineui.pages.room_pages.MainRoomScreen
import com.coursework.pleasantroutineui.repo.TestAccountRepo
import com.coursework.pleasantroutineui.repo.TestRoomRepo


@Composable
fun App() {
    val navController = rememberNavController()
    val repo = TestAccountRepo()
    val repoRoom = TestRoomRepo()

    val factory = AccountInfoViewModelFactory(repo)
    val factoryRoom = RoomViewModelFactory(repoRoom)

    val vm: AccountInfoViewModel = viewModel(factory = factory)
    val vmRoom: MainRoomPageViewModel = viewModel(factory = factoryRoom)

    NavHost(
        navController = navController,
        startDestination = Destinations.LOGIN_PAGE.title
    ) {
        composable(Destinations.USER_ACCOUNT_PAGE.title) { AccountInfoScreen(navController, vm) }
        composable(Destinations.LOGIN_PAGE.title) { RegistrationScreen(navController)}
        composable(Destinations.USER_ROOM_PAGE.title) { MainRoomScreen(navController, vmRoom)}
    }
}