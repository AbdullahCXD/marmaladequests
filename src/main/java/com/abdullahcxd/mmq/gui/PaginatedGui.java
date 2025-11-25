package com.abdullahcxd.mmq.gui;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class PaginatedGui extends Gui {

    private final List<GuiAction> items = new ArrayList<>();
    private int currentPage = 0;
    private int itemsPerPage;

    // Navigation button slots (default for 6-row inventory)
    private int previousButtonSlot = 45;
    private int nextButtonSlot = 53;
    private int closeButtonSlot = 49;

    // Navigation buttons
    private ItemStack previousButton;
    private ItemStack nextButton;
    private ItemStack closeButton;

    public PaginatedGui(Player player, String title, int size) {
        super(player, title, size);

        // Calculate items per page (excluding last row for navigation)
        this.itemsPerPage = size - 9;

        // Initialize default navigation buttons
        initializeDefaultButtons();
    }

    /**
     * Override this to add items to the paginated GUI
     */
    public abstract List<GuiAction> getPageItems();

    @Override
    public void init() {
        items.clear();
        items.addAll(getPageItems());

        displayPage(currentPage);
    }

    /**
     * Displays a specific page
     */
    public void displayPage(int page) {
        if (page < 0 || page >= getTotalPages()) {
            return;
        }

        this.currentPage = page;

        // Clear inventory
        clear();

        // Calculate start and end indices
        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, items.size());

        // Add items to inventory
        int slot = 0;
        for (int i = start; i < end; i++) {
            GuiAction action = items.get(i);
            setItem(slot, action.getItemStack(), action.getAction());
            slot++;
        }

        // Add navigation buttons
        addNavigationButtons();
    }

    /**
     * Adds navigation buttons to the last row
     */
    private void addNavigationButtons() {
        // Fill last row with border
        int lastRowStart = getSize() - 9;
        for (int i = lastRowStart; i < getSize(); i++) {
            setItem(i, createBorderItem());
        }

        // Previous button
        if (hasPreviousPage()) {
            setItem(previousButtonSlot, previousButton, (clicked, clicker) -> previousPage());
        }

        // Next button
        if (hasNextPage()) {
            setItem(nextButtonSlot, nextButton, (clicked, clicker) -> nextPage());
        }

        // Close button
        setItem(closeButtonSlot, closeButton, (clicked, clicker) -> close());
    }

    /**
     * Goes to the next page
     */
    public void nextPage() {
        if (hasNextPage()) {
            displayPage(currentPage + 1);
        }
    }

    /**
     * Goes to the previous page
     */
    public void previousPage() {
        if (hasPreviousPage()) {
            displayPage(currentPage - 1);
        }
    }

    /**
     * Checks if there's a next page
     */
    public boolean hasNextPage() {
        return currentPage < getTotalPages() - 1;
    }

    /**
     * Checks if there's a previous page
     */
    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    /**
     * Gets the total number of pages
     */
    public int getTotalPages() {
        if (items.isEmpty()) return 1;
        return (int) Math.ceil((double) items.size() / itemsPerPage);
    }

    /**
     * Refreshes the current page
     */
    public void refresh() {
        init();
    }

    /**
     * Sets custom navigation button slots
     */
    public PaginatedGui setNavigationSlots(int previousSlot, int nextSlot, int closeSlot) {
        this.previousButtonSlot = previousSlot;
        this.nextButtonSlot = nextSlot;
        this.closeButtonSlot = closeSlot;
        return this;
    }

    /**
     * Sets custom navigation buttons
     */
    public PaginatedGui setNavigationButtons(ItemStack previous, ItemStack next, ItemStack close) {
        this.previousButton = previous;
        this.nextButton = next;
        this.closeButton = close;
        return this;
    }

    /**
     * Creates default navigation buttons
     */
    private void initializeDefaultButtons() {
        // Previous button
        previousButton = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = previousButton.getItemMeta();
        if (prevMeta != null) {
            prevMeta.setDisplayName("§aPrevious Page");
            previousButton.setItemMeta(prevMeta);
        }

        // Next button
        nextButton = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextButton.getItemMeta();
        if (nextMeta != null) {
            nextMeta.setDisplayName("§aNext Page");
            nextButton.setItemMeta(nextMeta);
        }

        // Close button
        closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName("§cClose");
            closeButton.setItemMeta(closeMeta);
        }
    }

    /**
     * Creates a border item (glass pane)
     */
    private ItemStack createBorderItem() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }
}