package de.kevin.knockffa.inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

public class InventoryHelper {

    public static Inventory createInventory(String name, int lines, boolean prefilled) {
        Inventory inventory = Bukkit.createInventory(null, 9 * lines, name);
        if (prefilled) {
            ItemStack[] itemStacks = new ItemStack[lines * 9];
            for (int i = 0; i < lines * 9; i++)
                itemStacks[i] = getFiller();
            inventory.setContents(itemStacks);
        }
        return inventory;
    }

    public static ItemStack createItem(Material material, int typeId, int amount, String name, String... lore) {
        ItemStack itemStack = new ItemStack(material, amount, (short) typeId);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(lore));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack getBack() {
        return createTexturedSkull(HEADS.ARROW_LEFT, "§7§lStartseite", 1);
    }

    public static boolean isBack(ItemStack itm) {
        return getBack().equals(itm);
    }

    public static ItemStack getFiller() {
        return createItem(Material.STAINED_GLASS_PANE, 7, 1, "§r");
    }

    public static boolean isFiller(ItemStack itm) {
        return getFiller().equals(itm);
    }

    public enum HEADS {
        TOP10("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzM1NzE5NDQ1NjNlNjFhNzY1ZTk1ZWU4NGM4Y2Q3OWJmYjU4YTA3OGFhZDMzYmFiM2ExY2JhMzdlOGUzNDUwIn19fQ=="),
        ARROW_LEFT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE4NWM5N2RiYjgzNTNkZTY1MjY5OGQyNGI2NDMyN2I3OTNhM2YzMmE5OGJlNjdiNzE5ZmJlZGFiMzVlIn19fQ=="),
        ;

        private final String texture;

        HEADS(String s) {
            this.texture = s;
        }

        public String getTexture() {
            return texture;
        }
    }

    public static ItemStack createSkull(String owner, String itemName, int amount, String... lore) {
        ItemStack itemStack = createItem(Material.SKULL_ITEM, SkullType.PLAYER.ordinal(), amount, itemName, lore);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner(owner);
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    public static ItemStack createTexturedSkull(HEADS texture, String itemName, int amount, String... lore) {
        ItemStack itemStack = createItem(Material.SKULL_ITEM, SkullType.PLAYER.ordinal(), amount, itemName, lore);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture.getTexture()));
        Field profileField;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ignored) {

        }
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }


}
