package falseresync.wizcraft.client.render.entity;

import com.google.common.util.concurrent.Runnables;
import falseresync.wizcraft.common.data.attachment.WizcraftDataAttachments;
import falseresync.wizcraft.common.entity.EnergyVeilEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

public class EnergyVeilModel extends SinglePartEntityModel<PlayerEntity> {
    private final ModelPart root;
    private final ModelPart screen1;
    private final ModelPart screen2;
    private final ModelPart screen3;
    private final ModelPart screen4;

    public EnergyVeilModel(ModelPart root) {
        this.root = root;
        screen1 = root.getChild("screen1");
        screen2 = root.getChild("screen2");
        screen3 = root.getChild("screen3");
        screen4 = root.getChild("screen4");
    }

    public static TexturedModelData getTexturedModelData() {
        var modelData = new ModelData();
        var modelPartData = modelData.getRoot();
        var dilation = new Dilation(0, -2f, -2f);
        modelPartData.addChild(
                "screen1",
                ModelPartBuilder.create().uv(0, 0).cuboid(0f, -8f, -8f, 0.1f, 16f, 16f, dilation),
                ModelTransform.pivot(0, 8, -8));
        modelPartData.addChild(
                "screen2",
                ModelPartBuilder.create().uv(0, 0).cuboid(0f, -8f, -8f, 0.1f, 16f, 16f, dilation),
                ModelTransform.pivot(0, 24, -8));
        modelPartData.addChild(
                "screen3",
                ModelPartBuilder.create().uv(0, 0).cuboid(0f, -8f, -8f, 0.1f, 16f, 16f, dilation),
                ModelTransform.pivot(0, 16, 8));
        modelPartData.addChild(
                "screen4",
                ModelPartBuilder.create().uv(0, 0).cuboid(0f, -8f, -8f, 0.1f, 16f, 16f, dilation),
                ModelTransform.pivot(0, 32, 8));
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(PlayerEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public void animateModel(PlayerEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        getPart().traverse().forEach(ModelPart::resetTransform);
        Optional.ofNullable(entity.getAttached(WizcraftDataAttachments.ENERGY_VEIL_NETWORK_ID))
                .map(id -> entity.getEntityWorld().getEntityById(id))
                .flatMap(foundEntity -> foundEntity instanceof EnergyVeilEntity veil ? Optional.of(veil) : Optional.empty())
                .ifPresent(veil -> {
                    updateAnimation(veil.slideAnimationState, EnergyVeilAnimations.SLIDE, veil.age + tickDelta);
                    Runnables.doNothing();
                });
    }
}
