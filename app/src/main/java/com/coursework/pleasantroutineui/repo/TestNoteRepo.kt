package com.coursework.pleasantroutineui.repo

import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo

class TestNoteRepo: INotesRepo {
    val notes2 = arrayOf(
        Note(
            "q",
            "q",
            "2026-02-0q8",
            "2026-02-0q8",
            "Просто выходныеqqqqq",
            arrayOf("study"),
            arrayOf(""),
            "qqq"
        )
    )

    val notes1 = arrayOf(
        Note(
            "1",
            "0",
            "2026-02-08",
            "2026-02-08",
            "Просто выходные",
            arrayOf(
                "study",
                "interesting",
                "study",
                "interesting",
                "life",
                "cat",
                "work",
                "travel"
            ),
            arrayOf(
                "https://drive.google.com/uc?export=view&id=1xeXsy3qgqIG3T55eyo7Z69ZhgGi_dwGQ",
                "https://drive.google.com/uc?export=download&id=1Enn7rIvyCBbXTIXzy2jUshIwzy3BifMD",
                "https://drive.google.com/uc?export=download&id=1OJJrSNP3qSa_5Rjnj4B8nFYU7_CpK35f",
                "https://drive.google.com/uc?export=view&id=1xeXsy3qgqIG3T55eyo7Z69ZhgGi_dwGQ",
                "https://drive.google.com/uc?export=download&id=1Enn7rIvyCBbXTIXzy2jUshIwzy3BifMD",
                "https://drive.google.com/uc?export=download&id=1OJJrSNP3qSa_5Rjnj4B8nFYU7_CpK35f"
            ),
            "Сегодя писала курсовой проект. Это интересно, но работы очень много. Хоть уже лучше разбираюсь в языке, но как будто ни конца, ни края, хех."
        ),
        Note(
            "2",
            "0",
            "2026-02-06",
            "2026-02-06",
            "Веселая пятница",
            arrayOf("life", "study"),
            arrayOf(""),
            "Сегодня все как обычно, но хотя бы встретилась с друзьями. Вернулась поздно (больше на ту пару не пойду, скучно и слишком поздно прихжу в общагу)\nP.S. Кажется сейчас вообще все пары скучные("
        ),
        Note(
            "3",
            "0",
            "2026-02-09",
            "2026-02-09",
            "Веселая пятница2",
            arrayOf("life", "interesting", "work"),
            arrayOf(""),
            "Сегодня все как обычно, но хотя бы встретилась с друзьями. Вернулась поздно (больше на ту пару не пойду, скучно и слишком поздно прихжу в общагу)\nP.S. Кажется сейчас вообще все пары скучные("
        ),
        Note(
            "4",
            "0",
            "2026-02-16",
            "2026-02-16",
            "Веселая пятница4",
            arrayOf("work"),
            arrayOf(""),
            "Сегодня все как обычно, но хотя бы встретилась с друзьями. Вернулась поздно (больше на ту пару не пойду, скучно и слишком поздно прихжу в общагу)\nP.S. Кажется сейчас вообще все пары скучные("
        )
    )

    val tags1 = arrayOf("study", "interesting", "life", "cat", "work", "travel", "music", "food")
    override fun getAllNotes(ownerId: String): Array<Note> {
        if (ownerId == "0") {
            return notes1
        } else {
            return notes2
        }
    }

    override fun getAllTags(ownerId: String): Array<String> {
        return tags1
    }

    override fun getNote(id: String): Note {
        println(id)
        for (i in notes1) {
            if (i.id == id) {
                return i
            }
        }
        return notes2[0]
    }
}