package com.arugonoto.bloom.common.blocks;

import javax.annotation.Nullable;

import com.arugonoto.bloom.common.block_entities.MyceliumNucleusBlockEntity;
import com.arugonoto.bloom.common.registries.ModBlockEntities;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MyceliumNucleusBlock extends BaseEntityBlock {
	public MyceliumNucleusBlock(Properties properties) {
		super(properties);
	}

	@Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MyceliumNucleusBlockEntity(pos, state);
	}

	@Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(
            pBlockEntityType, 
            ModBlockEntities.MYCELIUM_NUCLEUS_BLOCK_ENTITY.get(),
            ((level, blockPos, blockState, tankBlockEntity) -> tankBlockEntity.tick(level, blockPos, blockState))
        );
    }  

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!level.isClientSide && level.getBlockEntity(pos) instanceof MyceliumNucleusBlockEntity be) {

            player.displayClientMessage(Component.literal(
				"Mycélium Nucleus - H : " + be.getHumidity() + " N : " + be.getNutrients() + " T : " + be.getTemperature() + " | Stock : " + be.getQuantity()
			), true);
        }

        return InteractionResult.SUCCESS;
	}
}
