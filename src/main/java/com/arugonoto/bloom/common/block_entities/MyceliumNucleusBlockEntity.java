package com.arugonoto.bloom.common.block_entities;

import java.util.ArrayList;

import com.arugonoto.bloom.common.core.AbstractNucleusBlockEntity;
import com.arugonoto.bloom.common.registries.ModBlockEntities;
import com.arugonoto.bloom.common.registries.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MyceliumNucleusBlockEntity extends AbstractNucleusBlockEntity {
	// Stats
    private int humidity = 80;
    private int nutrients = 60;
    private int temperature = 30;

    // Production
    private int prodTimer = 0;
    private int prodInterval = 20 * 10; 
    private int quantity = 0;
    private int maxStored = 64;

    // Overlay
    private int overlayColor = 0x7340D98C;

    // Effet
    private int radius = 7;

	public MyceliumNucleusBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.MYCELIUM_NUCLEUS_BLOCK_ENTITY.get(), pos, blockState);
	}

    @Override
    public int getHumidity() { return humidity; }
    @Override
    public int getNutrients() { return nutrients; }
    @Override
    public int getTemperature() { return temperature; }
    @Override
    public int getQuantity() { return quantity; }   
    @Override
    public int getRadius() { return radius; }

    @Override
    public int getOverlayColor() {
        return overlayColor;
    }

    @Override
	protected boolean conditionsMet() {
        return (humidity >= 60 && humidity <= 90) && (nutrients >= 30 && nutrients <= 60) && (temperature >= 0  && temperature <= 30);
    }

    @Override
	public void tick(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide) return;

        prodTimer++;
        if (prodTimer >= prodInterval) {
            prodTimer = 0;

            if (conditionsMet()) {
                if (quantity < maxStored) {
                    quantity++;
                    setChanged();
					sync();
                } else {
                    spawnOutput(level, pos);
                }
            }
        }
    }

    @Override
	protected void spawnOutput(Level level, BlockPos pos) {
        ItemStack out = new ItemStack(ModItems.SPORES.get(), 1);
        ItemEntity drop = new ItemEntity(level, pos.getX()+0.5, pos.getY()+1.1, pos.getZ()+0.5, out);
        level.addFreshEntity(drop);
    }

    @Override
	public ItemStack export(int requested) {
        if (requested <= 0 || quantity <= 0) return ItemStack.EMPTY;
        int take = Math.min(requested, quantity);
        quantity -= take;
        setChanged();
		sync();
        return new ItemStack(ModItems.SPORES.get(), take);
    }

	// Data Handling
	@Override
	protected void saveAdditional(ValueOutput output) {
        output.putInt("H", humidity);
        output.putInt("N", nutrients);
        output.putInt("T", temperature);
        output.putInt("ProdTimer", prodTimer);
        output.putInt("ProdInterval", prodInterval);
        output.putInt("Quantity", quantity);
        output.putInt("MaxStored", maxStored);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
        humidity = input.getIntOr("H", 80);
        nutrients = input.getIntOr("N", 60);
        temperature = input.getIntOr("T", 30);
        prodTimer = input.getIntOr("ProdTimer", 0);
        prodInterval = input.getIntOr("ProdInterval", 20 * 10);
        quantity = input.getIntOr("Quantity", 0);
        maxStored = input.getIntOr("MaxStored", 64);
	}
}
