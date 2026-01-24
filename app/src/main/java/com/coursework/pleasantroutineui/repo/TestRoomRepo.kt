package com.coursework.pleasantroutineui.repo
import com.coursework.pleasantroutineui.domain.User

// personal data of my cats lol
class TestRoomRepo: IRoomRepo {
    override fun getAllRoommates(numOfRoom: String): Array<User> {
        return arrayOf(User("12345678910",
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
            "https://drive.google.com/uc?export=view&id=1xeXsy3qgqIG3T55eyo7Z69ZhgGi_dwGQ"
            ),
            User("11111",
            "Мелкий",
            "Пушистый",
            "Валенок",
            "01.06.2019",
            "dramaqueen@gmail.com",
            "C081(1)",
            "Факультет кошачих наук",
            "Программная инженерия",
            "2 курс",
            "DO NOT TOUCH ME!",
            "https://drive.google.com/uc?export=download&id=1Enn7rIvyCBbXTIXzy2jUshIwzy3BifMD"),
            User("22222",
                "Пушок",
                "Меховые",
                "Лапы",
                "01.09.2022",
                "sunshine@gmail.com",
                "C081(1)",
                "Факультет кошачих наук",
                "Программная инженерия",
                "1 курс",
                "Люблю играть, мурчать и кушать криветки))))",
                "https://drive.google.com/uc?export=download&id=1OJJrSNP3qSa_5Rjnj4B8nFYU7_CpK35f"),
            User("22222",
                "Пушок",
                "Меховые",
                "Лапы",
                "01.09.2022",
                "sunshine@gmail.com",
                "C081(1)",
                "Факультет кошачих наук",
                "Программная инженерия",
                "1 курс",
                "Люблю играть, мурчать и кушать криветки))))",
                "https://drive.google.com/uc?export=download&id=1OJJrSNP3qSa_5Rjnj4B8nFYU7_CpK35f")

            )
    }
}