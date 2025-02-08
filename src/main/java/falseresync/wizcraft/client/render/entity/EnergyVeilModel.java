package falseresync.wizcraft.client.render.entity;

import falseresync.wizcraft.common.entity.EnergyVeilEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;

public class EnergyVeilModel extends SinglePartEntityModel<EnergyVeilEntity> {
    private final ModelPart root;
    private final ModelPart screen1;
    private final ModelPart screen2;
    private final ModelPart screen3;

    public EnergyVeilModel(ModelPart root) {
        this.root = root;
        screen1 = root.getChild("screen1");
        screen2 = root.getChild("screen2");
        screen3 = root.getChild("screen3");
    }

    public static TexturedModelData getTexturedModelData() {
        var modelData = new ModelData();
        var modelPartData = modelData.getRoot();
        modelPartData.addChild(
                "screen1",
                ModelPartBuilder.create().uv(0, 0).cuboid(0, 0, 0, 0.01f, 16, 16),
                ModelTransform.NONE);
        modelPartData.addChild(
                "screen2",
                ModelPartBuilder.create().uv(0, 0).cuboid(0, 0, -16, 0.01f, 16, 16),
                ModelTransform.NONE);
        modelPartData.addChild(
                "screen3",
                ModelPartBuilder.create().uv(0, 0).cuboid(0, 16, -8, 0.01f, 16, 16),
                ModelTransform.NONE);
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(EnergyVeilEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
//        updateAnimation();
    }
}
