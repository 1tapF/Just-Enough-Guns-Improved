package ttv.migami.jeg.common;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import ttv.migami.jeg.item.GunItem;
import ttv.migami.jeg.util.GunEnchantmentHelper;
import ttv.migami.jeg.util.GunModifierHelper;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * A class to track and control per-ItemStack weapon cooldowns.
 */
public class ShootTracker
{
    private static final Map<Player, ShootTracker> SHOOT_TRACKER_MAP = new WeakHashMap<>();

    private final Map<UUID, Pair<Long, Integer>> cooldownMap = Maps.newHashMap();

    public static ShootTracker getShootTracker(Player player)
    {
        return SHOOT_TRACKER_MAP.computeIfAbsent(player, player1 -> new ShootTracker());
    }

    public static UUID getOrCreateStackUUID(ItemStack stack)
    {
        if(stack.isEmpty()) return Util.NIL_UUID;
        CompoundTag tag = stack.getOrCreateTag();
        if(!tag.hasUUID("StackUUID"))
        {
            tag.putUUID("StackUUID", UUID.randomUUID());
        }
        return tag.getUUID("StackUUID");
    }

    public void putCooldown(ItemStack weapon, GunItem item, Gun modifiedGun)
    {
        int rate = GunEnchantmentHelper.getRate(weapon, modifiedGun);
        rate = GunModifierHelper.getModifiedRate(weapon, rate);
        putCustomCooldown(weapon, rate * 50);
    }

    public void putCustomCooldown(ItemStack weapon, int milliseconds)
    {
        if(weapon.isEmpty()) return;
        UUID id = getOrCreateStackUUID(weapon);
        this.cooldownMap.put(id, Pair.of(Util.getMillis(), milliseconds));
    }

    public boolean hasCooldown(ItemStack weapon)
    {
        if(weapon.isEmpty()) return false;
        UUID id = getOrCreateStackUUID(weapon);
        Pair<Long, Integer> pair = this.cooldownMap.get(id);
        if(pair != null)
        {
            return Util.getMillis() - pair.getLeft() < pair.getRight() - 50;
        }
        return false;
    }

    public long getRemaining(ItemStack weapon)
    {
        if(weapon.isEmpty()) return 0;
        UUID id = getOrCreateStackUUID(weapon);
        Pair<Long, Integer> pair = this.cooldownMap.get(id);
        if(pair != null)
        {
            return pair.getRight() - (Util.getMillis() - pair.getLeft());
        }
        return 0;
    }

    // Overloads for GunItem fallback compatibility
    public boolean hasCooldown(GunItem item) { return false; }
    public long getRemaining(GunItem item) { return 0; }
}
