package com.abdullahcxd.mmq.gui;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@Builder
@Setter
public class GuiAction {

    public interface ActionRunnable {

        void run(ItemStack clicked, Player clicker);

    }

    private ItemStack itemStack;
    private int slot;
    private ActionRunnable action;

}
