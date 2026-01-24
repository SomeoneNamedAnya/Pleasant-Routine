package com.coursework.pleasantroutineui.repo

import com.coursework.pleasantroutineui.domain.User

class TestAccountRepo: IAccountRepo {
    override fun getUser(num: Int): User {
        return User("12345678910",
            "Персик",
            "Принцесса",
            "Котейка",
            "08.03.2012",
            "cat@gmail.com",
            "C081(1)",
            "Факультет кошачих наук",
            "Программная инженерия",
            "Бакалавриат, 3 курс",
            "Люблю сметанку, креветки и рыбку",
            "https://drive.google.com/uc?export=download&id=1xeXsy3qgqIG3T55eyo7Z69ZhgGi_dwGQ"
            )
    }

    override fun getId(): String {
        return "12345678910"
    }

    override fun getName(): String {
        return "Персик"
    }

    override fun getSurname(): String {
        return "Принцесса"
    }

    override fun getLastname(): String {
        return "Котейка"
    }

    override fun getDateOfBirth(): String {
        return "08.03.2012"
    }

    override fun getEmail(): String {
        return "cat@gmail.com"
    }

    override fun getRoomNumber(): String {
        return "C081(1)"
    }

    override fun getDepartment(): String {
        return "Факультет кошачих наук"
    }

    override fun getEducationalProgram(): String {
        return "Программная инженерия"
    }

    override fun getEducationLevel(): String {
        return "Бакалавриат, 3 курс"
    }

    override fun getSelfInfo(): String {
        return "Люблю сметанку, креветки и рыбку"
    }

    override fun getPhotoLink(): String {
        return "https://drive.google.com/uc?export=download&id=1xeXsy3qgqIG3T55eyo7Z69ZhgGi_dwGQ"
    }
}