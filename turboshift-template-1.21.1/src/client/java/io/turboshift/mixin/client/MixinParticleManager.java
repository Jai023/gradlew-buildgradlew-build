package io.turboshift.mixin.client;

import io.turboshift.engine.ParticleOptimizer;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public class MixinParticleManager {
    @Inject(
        method = "addParticle(Lnet/minecraft/particle/ParticleEffect;ZDDDDDD)Lnet/minecraft/client/particle/Particle;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void turboshift$throttleParticles(
        ParticleEffect effect,
        boolean alwaysSpawn,
        double x, double y, double z,
        double dx, double dy, double dz,
        CallbackInfoReturnable<Particle> cir
    ) {
        if (!ParticleOptimizer.shouldSpawnParticle()) {
            cir.setReturnValue(null);
            return;
        }
        ParticleOptimizer.onParticleSpawned();
    }
}