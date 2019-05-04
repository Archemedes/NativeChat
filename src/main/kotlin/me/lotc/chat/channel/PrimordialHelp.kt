package me.lotc.chat.channel

import net.md_5.bungee.api.ChatColor.*

class PrimordialHelp : GlobalChannel("Help", "Help", "help", false, YELLOW, GOLD, false, 0, false) {

    override val permission get() = ""
    override val permissionTalk get() = ""
}