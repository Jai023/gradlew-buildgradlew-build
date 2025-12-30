package io.turboshift.mixin.client;

import io.turboshift.engine.ChunkAccelerator;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin. Mixin;
import org.spongepowered.asm. mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChunkBuilder.class)
public class MixinChunkBuilder {
    
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static int turboshift$optimizeChunkThreads(int threadCount) {
        return ChunkAccelerator.getOptimalThreadCount();
    }
}