package io.turboshift.mixin.client;

import io.turboshift.util. MemoryOptimizer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered. asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered. asm.mixin.injection. Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ClientWorld.class)
public class MixinClientWorld {
    
    @Inject(method = "tick", at = @At("RETURN"))
    private void turboshift$manageMemory(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        MemoryOptimizer.tick();
    }
}