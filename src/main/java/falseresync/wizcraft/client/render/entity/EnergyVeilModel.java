package falseresync.wizcraft.client.render.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.player.PlayerEntity;

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
                ModelPartBuilder.create().uv(0, 0).cuboid(0, 0, -14, 0.1f, 16, 16, dilation),
                ModelTransform.NONE);
        modelPartData.addChild(
                "screen2",
                ModelPartBuilder.create().uv(0, 0).cuboid(0, 16, -14, 0.1f, 16, 16, dilation),
                ModelTransform.NONE);
        modelPartData.addChild(
                "screen3",
                ModelPartBuilder.create().uv(0, 0).cuboid(0, 8, -1, 0.1f, 16, 16, dilation),
                ModelTransform.NONE);
        modelPartData.addChild(
                "screen4",
                ModelPartBuilder.create().uv(0, 0).cuboid(0, 24, -1, 0.001f, 16, 16, dilation),
                ModelTransform.NONE);
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(PlayerEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        // TODO: Start animation
        updateAnimation(EnergyVeilAnimations.SLIDE, animationProgress);
    }
}
