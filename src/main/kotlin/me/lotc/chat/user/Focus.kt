package me.lotc.chat.user


val MAX_HISTORY = 100

class Focus {

    enum class Category{
        NONE,
        STAFF,
        RP,
        OOC,
        GLOBAL,
        LOCAL,
        PM,
        SYSTEM,
        CHAT,
    }

}