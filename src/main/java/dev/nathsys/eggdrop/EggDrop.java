package dev.nathsys.silkspawner;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SilkSpawner extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("SilkTouchSpawner enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SilkTouchSpawner disabled!");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // Only handle spawners
        if (block.getType() == Material.SPAWNER) {
            ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();

            // Check for Silk Touch
            if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
                event.setDropItems(false);

                CreatureSpawner spawner = (CreatureSpawner) block.getState();
                ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
                block.getWorld().dropItemNaturally(block.getLocation(), spawnerItem);
            }
        }
    }
}