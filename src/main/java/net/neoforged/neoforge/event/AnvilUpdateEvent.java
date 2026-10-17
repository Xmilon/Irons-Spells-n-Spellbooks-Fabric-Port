package net.neoforged.neoforge.event;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public class AnvilUpdateEvent extends Event {
    private final ItemStack left;
    private final ItemStack right;
    private final String name;
    private ItemStack output = ItemStack.EMPTY;
    private int cost;
    private int materialCost;

    public AnvilUpdateEvent(ItemStack left, ItemStack right, String name) {
        this.left = left;
        this.right = right;
        this.name = name;
    }

    public ItemStack getLeft() { return left; }
    public ItemStack getRight() { return right; }
    public String getName() { return name; }
    public ItemStack getOutput() { return output; }
    public void setOutput(ItemStack output) { this.output = output; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public int getMaterialCost() { return materialCost; }
    public void setMaterialCost(int materialCost) { this.materialCost = materialCost; }
}
