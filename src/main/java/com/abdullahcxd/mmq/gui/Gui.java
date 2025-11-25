package com.abdullahcxd.mmq.gui;

import com.abdullahcxd.mmq.listeners.MQListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Gui extends MQListener {

    private final Inventory inventory;
    private final Player player;
    private final List<GuiAction> actions = new ArrayList<>();

    public Gui(Player player, String title, int size) {
        this.player = player;
        this.inventory = Bukkit.createInventory(player, size, title);
    }

    public abstract void init();

    public Gui registerAction(GuiAction action) {
        this.actions.add(action);
        return this;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) return;

        Player clicker = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        ItemStack clicked = event.getCurrentItem();

        for (GuiAction action : actions) {
            if (action.getSlot() == slot && action.getAction() != null) {
                action.getAction().run(clicked, clicker);
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        event.setCancelled(true);
    }

    public void open() {
        init();
        player.openInventory(inventory);
    }

    public void close() {
        player.closeInventory();
    }

    // Helper Methods

    /**
     * Sets an item at a specific slot
     */
    public Gui setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
        return this;
    }

    /**
     * Sets an item at a specific slot with an action
     */
    public Gui setItem(int slot, ItemStack item, GuiAction.ActionRunnable action) {
        inventory.setItem(slot, item);
        GuiAction guiAction = GuiAction.builder()
                .slot(slot)
                .itemStack(item)
                .action(action)
                .build();
        registerAction(guiAction);
        return this;
    }

    /**
     * Fills the entire inventory with an item
     */
    public Gui fill(ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, item);
        }
        return this;
    }

    /**
     * Fills the border of the inventory with an item
     */
    public Gui fillBordersWith(ItemStack item) {
        int size = inventory.getSize();
        int rows = size / 9;

        // Fill top and bottom rows
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, item);
            inventory.setItem(size - 9 + i, item);
        }

        // Fill left and right columns
        for (int i = 1; i < rows - 1; i++) {
            inventory.setItem(i * 9, item);
            inventory.setItem(i * 9 + 8, item);
        }

        return this;
    }

    /**
     * Fills a specific row with an item
     */
    public Gui fillRow(int row, ItemStack item) {
        int start = row * 9;
        for (int i = 0; i < 9; i++) {
            inventory.setItem(start + i, item);
        }
        return this;
    }

    /**
     * Fills a specific column with an item
     */
    public Gui fillColumn(int column, ItemStack item) {
        int rows = inventory.getSize() / 9;
        for (int i = 0; i < rows; i++) {
            inventory.setItem(i * 9 + column, item);
        }
        return this;
    }

    /**
     * Fills a rectangular area with an item
     */
    public Gui fillArea(int startSlot, int endSlot, ItemStack item) {
        for (int i = startSlot; i <= endSlot && i < inventory.getSize(); i++) {
            inventory.setItem(i, item);
        }
        return this;
    }

    /**
     * Clears the entire inventory
     */
    public Gui clear() {
        inventory.clear();
        return this;
    }

    /**
     * Clears a specific slot
     */
    public Gui clearSlot(int slot) {
        inventory.setItem(slot, null);
        return this;
    }

    /**
     * Gets the size of the inventory
     */
    public int getSize() {
        return inventory.getSize();
    }

    /**
     * Gets the number of rows in the inventory
     */
    public int getRows() {
        return inventory.getSize() / 9;
    }
}