package com.luna.jetoverlay.armor;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class JetGoggles implements ArmorMaterial {
	public static final JetGoggles INSTANCE = new JetGoggles();
	public static final String GOGGLES_BLOCK_TAG_NAME = "blocks";

	@Override
	public int getDurabilityForType(ArmorItem.Type type) {
		return 0;
	}

	@Override
	public int getDefenseForType(ArmorItem.Type type) {
		return 1;
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

	@Override
	public @NotNull SoundEvent getEquipSound() {
		return SoundEvents.ARMOR_EQUIP_GENERIC;
	}

	@Override
	public @NotNull Ingredient getRepairIngredient() {
		return Ingredient.EMPTY;
	}

	@Override
	public @NotNull String getName() {
		return "jetgoggles";
	}

	@Override
	public float getToughness() {
		return 0;
	}

	@Override
	public float getKnockbackResistance() {
		return 0;
	}

	@Nullable
	private static CompoundTag getBlocksCompound(ItemStack __from) {
		if (!__from.hasTag()
				|| !Objects.requireNonNull(__from.getTag()).contains(GOGGLES_BLOCK_TAG_NAME, Tag.TAG_COMPOUND)) {
			return null;
		}
		return __from.getTag().getCompound(GOGGLES_BLOCK_TAG_NAME);
	}

	private static String getBlockHash(BlockPos __block) {
		return String.format("block{%s}", __block.hashCode());
	}

	public static ArrayList<BlockPos> itemGetBlocks(ItemStack __from) {
		final CompoundTag blocksCompound = getBlocksCompound(__from);
		if (blocksCompound == null) {
			return new ArrayList<>();
		}

		final Set<String> keys = blocksCompound.getAllKeys();
		final ArrayList<BlockPos> blocks = new ArrayList<>();

		for (var key : keys) {
			int[] positionValues = blocksCompound.getIntArray(key);
			if (positionValues.length == 0) {
				break;
			}
			blocks.add(new BlockPos(positionValues[0], positionValues[1], positionValues[2]));
		}

		return blocks;
	}

	public static void itemAddBlock(ItemStack __from, BlockPos __block) {
		CompoundTag blocksCompound = getBlocksCompound(__from);
		if (blocksCompound == null) {
			blocksCompound = new CompoundTag();
			__from.addTagElement(GOGGLES_BLOCK_TAG_NAME, blocksCompound);
		}

		final int[] coordinates = new int[]{__block.getX(), __block.getY(), __block.getZ()};
		blocksCompound.putIntArray(getBlockHash(__block), coordinates);
	}

	public static boolean itemRemoveBlock(ItemStack __from, BlockPos __block) {
		final CompoundTag blocksCompound = getBlocksCompound(__from);
		if (blocksCompound == null) {
			return false;
		}

		final String blockHash = getBlockHash(__block);

		if (!blocksCompound.contains(blockHash)) {
			return false;
		}

		blocksCompound.remove(blockHash);
		return true;
	}

	public static boolean itemHasBlock(ItemStack __from, BlockPos __block) {
		final CompoundTag blocksCompound = getBlocksCompound(__from);
		if (blocksCompound == null) {
			return false;
		}

		return blocksCompound.contains(getBlockHash(__block));
	}
}
