package com.coursework.pleasantroutineui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.pages.entrance.FastRoute
import com.coursework.pleasantroutineui.pages.entrance.FastViewModel
import com.coursework.pleasantroutineui.pages.entrance.LogoutRoute
import com.coursework.pleasantroutineui.pages.entrance.LogoutViewModel
import com.coursework.pleasantroutineui.pages.profile.AccountInfoScreen
import com.coursework.pleasantroutineui.pages.profile.AllUserInfoViewModel
import com.coursework.pleasantroutineui.pages.entrance.RegistrationScreen
import com.coursework.pleasantroutineui.pages.entrance.RegistrationViewModel
import com.coursework.pleasantroutineui.pages.profile.AccountInfoViewModel
import com.coursework.pleasantroutineui.pages.room.MainRoomPageViewModel
import com.coursework.pleasantroutineui.pages.room.MainRoomScreen


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

    }
}