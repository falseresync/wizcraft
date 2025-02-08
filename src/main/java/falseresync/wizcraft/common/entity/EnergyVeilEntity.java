package falseresync.wizcraft.common.entity;

import falseresync.wizcraft.common.Wizcraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnergyVeilEntity extends Entity implements Ownable {
    private static final TrackedData<Float> WIDTH = DataTracker.registerData(EnergyVeilEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> HEIGHT = DataTracker.registerData(EnergyVeilEntity.class, TrackedDataHandlerRegistry.FLOAT);
    @Nullable
    private PlayerEntity owner;

    public EnergyVeilEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public EnergyVeilEntity(@Nullable PlayerEntity owner, World world) {
        this(WizcraftEntities.ENERGY_VEIL, world);
        this.owner = owner;
        alignWithOwner();
    }

    public void alignWithOwner() {
        if (owner == null) return;
        var rotation = owner.getRotationVec(1);
        var orthogonalDistance = 2;
        var pos = owner.getPos();
        updatePosition(pos.x + rotation.x * orthogonalDistance, pos.y,pos.z + rotation.z * orthogonalDistance);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(WIDTH, 1.0F);
        builder.add(HEIGHT, 1.0F);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (HEIGHT.equals(data) || WIDTH.equals(data)) {
            this.setBoundingBox(this.calculateBoundingBox());
        }
    }

    @Override
    public void tick() {
        super.tick();
        alignWithOwner();
    }

    public final void setVeilWidth(float width) {
        dataTracker.set(WIDTH, width);
    }

    public final float getVeilWidth() {
        return dataTracker.get(WIDTH);
    }

    public final void setVeilHeight(float height) {
        dataTracker.set(HEIGHT, height);
    }

    public final float getVeilHeight() {
        return dataTracker.get(HEIGHT);
    }

    private EntityDimensions getDimensions() {
        return EntityDimensions.changing(getVeilWidth(), getVeilHeight());
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return getDimensions();
    }

    @Override
    protected Box calculateBoundingBox() {
        return getDimensions().getBoxAt(getPos());//.contract(getVeilWidth() / 2 - 0.01f, 0, 0);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("width", NbtElement.FLOAT_TYPE)) {
            setVeilHeight(nbt.getFloat("width"));
        }

        if (nbt.contains("height", NbtElement.FLOAT_TYPE)) {
            setVeilHeight(nbt.getFloat("height"));
        }

        if (nbt.contains("owner")) {
            Uuids.INT_STREAM_CODEC.decode(NbtOps.INSTANCE, nbt.get("owner"))
                    .resultOrPartial(Util.addPrefix("Could not decode a Player UUID of ", Wizcraft.LOGGER::error))
                    .ifPresent(pair -> owner = getEntityWorld().getPlayerByUuid(pair.getFirst()));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("width", getVeilWidth());
        nbt.putFloat("height", getVeilHeight());
        if (owner != null) {
            Uuids.INT_STREAM_CODEC.encodeStart(NbtOps.INSTANCE, owner.getUuid()).ifSuccess(it -> nbt.put("owner", it));
        }
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        return super.handleAttack(attacker);
    }

    @Nullable
    @Override
    public PlayerEntity getOwner() {
        return owner;
    }
}
