package me.elsiff.morefish.hooker;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import org.bukkit.Location;
import org.bukkit.World;

public final class WorldGuardHooker implements PluginHooker {

    private boolean hasHooked;

    public final boolean containsLocation(@Nonnull String regionId, @Nonnull Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        World world = location.getWorld();
        if (world == null) {
            return false;
        }

        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        if (regionManager == null) {
            return false;
        }

        ProtectedRegion region = regionManager.getRegion(regionId);
        if (region == null) {
            throw new IllegalStateException("Region " + regionId + " doesn't exist");
        }

        return region.contains(x, y, z);
    }

    @Nonnull
    public String getPluginName() {
        return "WorldGuard";
    }

    public boolean hasHooked() {
        return this.hasHooked;
    }

    public void hook(@Nonnull MoreFish plugin) {
        this.setHasHooked(true);
    }

    public void setHasHooked(boolean var1) {
        this.hasHooked = var1;
    }
}
