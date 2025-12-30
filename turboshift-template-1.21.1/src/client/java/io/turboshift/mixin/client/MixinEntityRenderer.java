package io.turboshift.mixin.client;

import io.turboshift.core. PerformanceTracker;
import io.turboshift.engine.EntityCullingSystem;
import net.minecraft.client.MinecraftClient;
import net. minecraft.client.render.entity. EntityRenderer;
import net.minecraft.client.render. Frustum;
import net.minecraft. entity.Entity;
import org.spongepowered.asm. mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm. mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void turboshift$optimizeEntityRendering(Entity entity, 
                                                    Frustum frustum,
                                                    double x, double y, double z,
                                                    CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client. gameRenderer == null || client.gameRenderer.getCamera() == null) {
            return;
        }
        
        boolean shouldRender = EntityCullingSystem.shouldRenderEntity(
                entity, 
                client.gameRenderer.getCamera().getPos()
        );
        
        if (! shouldRender) {
            cir.setReturnValue(false);
        }
    }
}