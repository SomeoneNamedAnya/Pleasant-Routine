package com.coursework.pleasantroutineui.repo
import com.coursework.pleasantroutineui.domain.RoomInfo
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.repo.interfaces.IRoomRepo
import com.coursework.pleasantroutineui.repo.interfaces.IUsers

// personal data of my cats lol
class TestRoomRepo: IRoomRepo {
    val userService: IUsers = TestUsers()

    override fun getAllRoommates(numOfRoom: String): Array<User> {

        return userService.getUsers()
    }

    override fun getRoomInfo(numOfRoom: String): RoomInfo {
        return RoomInfo(
            roomNumber = "C081",
            dormitoryName = "Общежитие №8",
            publicDescription = "Дружная и пушистая кмната лучших котов",
            photoLinks = listOf("https://drive.google.com/uc?export=view&id=1Ju7knzGn2c-YFkSBZZ7eK3jyL42vnZfi"),
            residents = userService.getUsers().toList(),
            roomRules = "В этой комнате проживают лучшие и самые пушистые кошки. Вот несколько правил для комфортного проживания:\n1) Никакой рыбы на кровати \n2) Никаких вкусняшек на подоконнике \n3) Никакого воровства вкусняшек и криветок \n4) Вычесываться нужно каждый день, а купаться хотя бы раз в месяц"
        )
    }
}