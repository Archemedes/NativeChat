package me.lotc.chat

import me.lotc.chat.channel.Channel
import me.lotc.chat.channel.YamlChannelBuilder
import me.lotc.chat.command.ChatCommand
import co.lotc.core.bukkit.command.Commands
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.lang.Exception
import java.util.function.Supplier

class NativeChat : JavaPlugin() {
    companion object {
        var instance: NativeChat? = null
            private set
        fun get() = instance!!
    }

    val chatManager = ChatManager()

    override fun onLoad(){
        instance = this
    }

    override fun onEnable() {
        saveDefaultConfig()
        loadChannels()

        registerChannelParameterType()
        Commands.build(getCommand("chat"), Supplier { ChatCommand() })

        Bukkit.getPluginManager().registerEvents(ChatListener(this), this)
        FocusListener.listen(this)
        BungeeListener.listen(this)

        Bukkit.getOnlinePlayers().forEach(chatManager::join)
    }

    override fun onDisable(){
        Bukkit.getOnlinePlayers().forEach(chatManager::quit)
    }

    private fun loadChannels() {
        val channels = ArrayList<Channel>()

        channels.addAll(buildChannels("global", YamlChannelBuilder::asGlobalChannel))
        channels.addAll(buildChannels("local", YamlChannelBuilder::asLocalChannel))
        channels.addAll(buildChannels("broadcast", YamlChannelBuilder::asBroadcastChannel))
        
        chatManager.onEnable( channels )
    }

    private fun buildChannels(sectionName: String, builder : (data: YamlChannelBuilder) -> Channel) : List<Channel> {
        val channels = ArrayList<Channel>()

        val config = this.config
        val channelSection = config.getConfigurationSection(sectionName) ?: return channels

        channelSection.getKeys(false).forEach { try {
            val data = YamlChannelBuilder(channelSection.getConfigurationSection(it)!!)
            val channel = builder.invoke(data)
            channels.add(channel)
        } catch (e: Exception) { e.printStackTrace() } }

        return channels
    }

    private fun registerChannelParameterType() {
        Commands.defineArgumentType(Channel::class.java)
            .defaultName("Channel")
            .defaultError("Could not find this channel")
            .mapperWithSender { _,i -> chatManager.getByAlias(i) }
            .completer { s,_->chatManager.channels.filter { s.hasPermission(it.permission) }.map { it.cmd } }
            .register()
    }
}