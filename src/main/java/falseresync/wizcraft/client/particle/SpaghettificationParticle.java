package falseresync.wizcraft.client.particle;

import net.fabricmc.api.*;
import net.minecraft.client.*;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.*;
import net.minecraft.item.*;
import net.minecraft.particle.*;

/**
 * A mimic of {@link CrackParticle}
 */
@Environment(EnvType.CLIENT)
public class SpaghettificationParticle extends SpriteBillboardParticle {
    private final float sampleU;
    private final float sampleV;

    protected SpaghettificationParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ItemStack stack) {
        super(world, x, y, z, 0, 0, 0);
        this.setSprite(MinecraftClient.getInstance().getItemRenderer().getModel(stack, world, null, 0).getParticleSprite());
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.maxAge = 5;
        this.collidesWithWorld = false;
        this.gravityStrength = 0.05f;
        this.scale /= 4.0F;
        this.sampleU = world.random.nextFloat() * 3.0F;
        this.sampleV = world.random.nextFloat() * 3.0F;
    }

    public static ParticleFactory<ItemStackParticleEffect> getFactory() {
        return (particle, world, x, y, z, velocityX, velocityY, velocityZ) ->
                new SpaghettificationParticle(world, x, y, z, velocityX, velocityY, velocityZ, particle.getItemStack());
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.TERRAIN_SHEET;
    }

    @Override
    protected float getMinU() {
        return this.sprite.getFrameU((this.sampleU + 1.0F) / 4.0F);
    }

    @Override
    protected float getMaxU() {
        return this.sprite.getFrameU(this.sampleU / 4.0F);
    }

    @Override
    protected float getMinV() {
        return this.sprite.getFrameV(this.sampleV / 4.0F);
    }

    @Override
    protected float getMaxV() {
        return this.sprite.getFrameV((this.sampleV + 1.0F) / 4.0F);
    }
}