package net.ilexiconn.llibrary.server.capability;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @author gegy1000
 * @since 1.0.0
 */
public interface IEntityDataCapability {
    /**
     * Initialize this data context.
     *
     * @param entity the new entity
     * @param world  the new world
     * @param init   true if all managers should be initialized
     */
    void init(Entity entity, World world, boolean init);

    /**
     * Saves this capability to NBT.
     *
     * @param compound the tag to write to
     */
    void saveToNBT(NBTTagCompound compound);

    /**
     * Loads this capability from NBT.
     *
     * @param compound the tag to read from
     */
    void loadFromNBT(NBTTagCompound compound);
}
