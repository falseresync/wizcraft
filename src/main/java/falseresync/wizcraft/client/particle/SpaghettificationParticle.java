package falseresync.wizcraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.world.item.ItemStack;

/**
 * A mimic of {@link BreakingItemParticle}
 */
@Environment(EnvType.CLIENT)
public class SpaghettificationParticle extends TextureSheetParticle {
    private final float sampleU;
    private final float sampleV;

    protected SpaghettificationParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, ItemStack stack) {
        super(level, x, y, z, 0, 0, 0);
        this.setSprite(Minecraft.getInstance().getItemRenderer().getModel(stack, level, null, 0).getParticleIcon());
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.lifetime = 5;
        this.hasPhysics = false;
        this.gravity = 0.05f;
        this.quadSize /= 4.0F;
        this.sampleU = level.random.nextFloat() * 3.0F;
        this.sampleV = level.random.nextFloat() * 3.0F;
    }

    public static ParticleProvider<ItemParticleOption> makeProvider() {
        return (particle, world, x, y, z, velocityX, velocityY, velocityZ) ->
                new SpaghettificationParticle(world, x, y, z, velocityX, velocityY, velocityZ, particle.getItem());
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU((this.sampleU + 1.0F) / 4.0F);
    }

    @Override
    protected float getU1() {
        return this.sprite.getU(this.sampleU / 4.0F);
    }

    @Override
    protected float getV0() {
        return this.sprite.getV(this.sampleV / 4.0F);
    }

    @Override
    protected float getV1() {
        return this.sprite.getV((this.sampleV + 1.0F) / 4.0F);
    }
}