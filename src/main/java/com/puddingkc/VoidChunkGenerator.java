package com.puddingkc;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * @author Coaixy
 * @createTime 2024-08-20
 * @packageName com.puddingkc
 */

public class VoidChunkGenerator extends ChunkGenerator {
    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        // 创建一个空的区块数据对象
        return createChunkData(world);
    }
}

