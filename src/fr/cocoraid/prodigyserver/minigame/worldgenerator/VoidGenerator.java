package fr.cocoraid.prodigyserver.minigame.worldgenerator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class VoidGenerator extends ChunkGenerator {


    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 0, 0);
    }


    public final ChunkGenerator.ChunkData generateChunkData(final World world, final Random random, final int ChunkX, final int ChunkZ, final ChunkGenerator.BiomeGrid biome) {
        final ChunkGenerator.ChunkData chunkData = this.createChunkData(world);
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                biome.setBiome(x, z, Biome.JUNGLE);
            }
        }
        if (0 >= ChunkX << 4 && 0 < ChunkX + 1 << 4 && 0 >= ChunkZ << 4 && 0 < ChunkZ + 1 << 4) {
            chunkData.setBlock(0, 63, 0, Material.BEDROCK);
        }
        return chunkData;
    }

}