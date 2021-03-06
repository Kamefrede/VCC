package me.gammadelta.common.block;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import me.gammadelta.common.block.tile.TileChassis;
import me.gammadelta.common.block.tile.TileMotherboard;

import static me.gammadelta.VCCMod.MOD_ID;

public class VCCBlocks {
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
	public static final RegistryObject<Block> MOTHERBOARD_BLOCK = BLOCKS.register(BlockMotherboard.NAME,
			BlockMotherboard::new);
	public static final RegistryObject<Block> CHASSIS_BLOCK = BLOCKS.register(BlockChassis.NAME,
			() -> new BlockChassis());
	private static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(
			ForgeRegistries.TILE_ENTITIES, MOD_ID);

	public static final RegistryObject<TileEntityType<TileMotherboard>> MOTHERBOARD_TILE = TILES.register(
			BlockMotherboard.NAME,
			() -> TileEntityType.Builder.create(TileMotherboard::new, MOTHERBOARD_BLOCK.get()).build(null));
	public static final RegistryObject<TileEntityType<TileChassis>> CHASSIS_TILE = TILES.register(
			BlockChassis.NAME,
			() -> TileEntityType.Builder.create(TileChassis::new, CHASSIS_BLOCK.get()).build(null));

	public static void register(){
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
		TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
