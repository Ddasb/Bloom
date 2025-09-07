package com.arugonoto.bloom.common.core;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

abstract public class AbstractNucleusBlockEntity extends BlockEntity {
    public AbstractNucleusBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    abstract public int getHumidity();
    abstract public int getNutrients();
    abstract public int getTemperature();
    abstract public int getQuantity();
    abstract public int getRadius();

    abstract public int getOverlayColor();

    abstract protected boolean conditionsMet();

    abstract public void tick(Level level, BlockPos pos, BlockState state);

    abstract protected void spawnOutput(Level level, BlockPos pos);

    abstract public ItemStack export(int requested);

    public void sync() {
		if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
	}

    abstract protected void saveAdditional(ValueOutput output);
    abstract protected void loadAdditional(ValueInput input);

	@Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
    }
	
    @Override
    public void onDataPacket(Connection connection, ValueInput input) {
        super.onDataPacket(connection, input);
    }
}
