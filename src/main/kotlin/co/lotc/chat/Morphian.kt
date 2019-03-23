package co.lotc.chat

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Morphian : JavaPlugin() {
    lateinit var chatManager : ChatManager

    override fun onEnable() {
        loadChannels()
        Bukkit.getOnlinePlayers().forEach(chatManager::join)
    }

    override fun onDisable(){
        Bukkit.getOnlinePlayers().forEach(chatManager::quit)
    }

    private fun loadChannels() {
        //TODO: read from config.yml per-channel settings
        //TODO: this will need to construct the chatManager
    }
}