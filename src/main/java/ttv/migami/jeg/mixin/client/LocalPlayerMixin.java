package ttv.migami.jeg.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ttv.migami.jeg.client.handler.AimingHandler;

@Mixin(LivingEntity.class)
public class LocalPlayerMixin {

    @Inject(method = "setSprinting(Z)V", at = @At("HEAD"), cancellable = true)
    private void onSetSprinting(boolean sprinting, CallbackInfo ci) {
        if (sprinting && ((Object) this instanceof LocalPlayer) && AimingHandler.get().isAiming()) {
            ci.cancel();
        }
    }
}
