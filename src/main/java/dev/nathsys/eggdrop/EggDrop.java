package dev.nathsys.eggdrop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EggDrop extends JavaPlugin implements Listener {

    private final Random random = new Random();
    private final Map<EntityType, Double> mobChances = new HashMap<>();

    private double defaultChance;
    private boolean playerKillOnly;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();

        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadConfigValues() {
        defaultChance = getConfig().getDouble("default-chance");
        playerKillOnly = getConfig().getBoolean("player-kill-only");

        mobChances.clear();

        ConfigurationSection section = getConfig().getConfigurationSection("mob-chances");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                EntityType type = EntityType.valueOf(key.toUpperCase());
                double chance = section.getDouble(key);
                mobChances.put(type, chance);
            } catch (IllegalArgumentException e) {
                getLogger().warning("Invalid mob in config: " + key);
            }
        }
    }

    // Perform rng check and drop if success.
    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {

        if (playerKillOnly && event.getEntity().getKiller() == null) return;

        EntityType type = event.getEntityType();

        double chance = mobChances.getOrDefault(type, defaultChance);

        if (random.nextDouble() > chance) return;

        Material eggMaterial = Material.matchMaterial(type.name() + "_SPAWN_EGG");
        if (eggMaterial == null) return;

        ItemStack egg = new ItemStack(eggMaterial, 1);
        event.getDrops().add(egg);

        String mobName = formatEntityName(type);
        Component message;

        if (event.getEntity().getKiller() != null) {
            String playerName = event.getEntity().getKiller().getName();

            message = Component.text(playerName, NamedTextColor.YELLOW)
                    .append(Component.text(" obtained a ", NamedTextColor.GRAY))
                    .append(Component.text(mobName, NamedTextColor.GREEN))
                    .append(Component.text(" spawn egg.", NamedTextColor.GRAY));
        } else {
            message = Component.text("A ", NamedTextColor.GRAY)
                    .append(Component.text(mobName, NamedTextColor.GREEN))
                    .append(Component.text(" spawn egg was dropped.", NamedTextColor.GRAY));
        }

        Bukkit.getServer().broadcast(message);
    }

    private String formatEntityName(EntityType type) {
        String name = type.name().toLowerCase().replace("_", " ");
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}