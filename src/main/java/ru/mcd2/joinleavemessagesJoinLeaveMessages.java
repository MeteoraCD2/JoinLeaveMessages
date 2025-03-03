package ru.mcd2.joinleavemessages;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinLeaveMessages extends JavaPlugin implements Listener {
    private boolean placeholderAPIEnabled = false;
    private String messageColor;
    private String joinMessageFormat;
    private String leaveMessageFormat;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        // Load configuration
        loadConfig();
        
        // Check for PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderAPIEnabled = true;
            getLogger().info("PlaceholderAPI found and hooked successfully!");
        } else {
            getLogger().warning("PlaceholderAPI not found! Placeholders will not work.");
        }
        
        // Register event listener
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("JoinLeaveMessages plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JoinLeaveMessages plugin has been disabled!");
    }
    
    private void loadConfig() {
        // Reload config from file
        reloadConfig();
        
        // Get values from config
        messageColor = getConfig().getString("message-color", "&e");
        joinMessageFormat = getConfig().getString("join-message", "[+] %player_name%");
        leaveMessageFormat = getConfig().getString("leave-message", "[-] %player_name%");
        
        // Log loaded configuration
        getLogger().info("Configuration loaded:");
        getLogger().info("Message Color: " + messageColor);
        getLogger().info("Join Message: " + joinMessageFormat);
        getLogger().info("Leave Message: " + leaveMessageFormat);
    }
    
    private String formatMessage(String format, Player player) {
        // Replace %player_name% with player's name
        String message = format.replace("%player_name%", player.getName());
        
        // Apply PlaceholderAPI if available
        if (placeholderAPIEnabled) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        
        // Apply color codes
        return ChatColor.translateAlternateColorCodes('&', messageColor + message);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Cancel the default join message
        event.setJoinMessage(null);
        
        // Send custom join message to all players
        String message = formatMessage(joinMessageFormat, event.getPlayer());
        Bukkit.broadcastMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cancel the default quit message
        event.setQuitMessage(null);
        
        // Send custom leave message to all players
        String message = formatMessage(leaveMessageFormat, event.getPlayer());
        Bukkit.broadcastMessage(message);
    }
}
