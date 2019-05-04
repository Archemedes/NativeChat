package me.lotc.chat.user


const val MAX_HISTORY = 100

class Focus {

    enum class Category{
        NONE,
        MENTION,
        STAFF,
        RP,
        OOC,
        PM,
        SYSTEM,
        CHAT,
    }
}