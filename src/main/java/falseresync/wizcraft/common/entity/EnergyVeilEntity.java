package falseresync.wizcraft.common.entity;

import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.data.attachment.WizcraftDataAttachments;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.BlockAttachedEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergyVeilEntity extends Entity implements Ownable {
    private static final TrackedData<Float> RADIUS = DataTracker.registerData(EnergyVeilEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final float SCREENS_OFFSET = 0.25f;
    public AnimationState slideAnimationState = new AnimationState();
    private int lifeExpectancy;
    @Nullable
    private PlayerEntity owner;

    public EnergyVeilEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public EnergyVeilEntity(@Nullable PlayerEntity owner, World world) {
        this(WizcraftEntities.ENERGY_VEIL, world);
        this.owner = owner;

        lifeExpectancy = 20;
        noClip = true;
        movementMultiplier = new Vec3d(1, 1, 1);
        setNoGravity(true);

        if (owner != null) {
            setPosition(owner.getPos());
        }
        alignWithOwner();
    }

    public void incrementLifeExpectancy(int by) {
        lifeExpectancy += by;
    }

    public void alignWithOwner() {
        if (owner == null) return;
        setVelocity(owner.getPos().subtract(getPos()));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(RADIUS, 1.0F);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (RADIUS.equals(data)) {
            setBoundingBox(calculateBoundingBox());
        }
        if (owner != null) {
            owner.setAttached(WizcraftDataAttachments.ENERGY_VEIL_NETWORK_ID, getId());
        }
        if (getEntityWorld().isClient()) {
            slideAnimationState.startIfNotRunning(age);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (age == lifeExpectancy) {
            slideAnimationState.stop();
            discard();
            if (owner != null) {
                owner.removeAttached(WizcraftDataAttachments.ENERGY_VEIL_NETWORK_ID);
            }
        }
        alignWithOwner();
        move(MovementType.SELF, getMovement());

        if (!getWorld().isClient) {
            var entities = getWorld().getOtherEntities(this, getBoundingBox(), EntityPredicates.EXCEPT_SPECTATOR
                    .and(it -> !it.equals(owner))
                    .and(it -> !(it instanceof Ownable ownable && owner != null && owner.equals(ownable.getOwner())))
                    .and(it -> !it.getType().isIn(WizcraftEntityTags.PASSES_THROUGH_ENERGY_VEIL))
                    .and(it -> !(it instanceof VehicleEntity vehicle && !vehicle.hasPassengers()))
                    .and(it -> !(it instanceof DisplayEntity || it instanceof BlockAttachedEntity)));
            for (Entity entity : entities) {
                if (entity.getPos().squaredDistanceTo(getPos()) <= Math.pow(getVeilRadius(), 2)) {
                    // dot product shows the direction. if it's negative - vectors are pointing opposite of each other
                    // if it's 0 - they point orthogonally, and if it's positive - more or less in the same direction
                    // compare the entity velocity and the entity-to-veil-center vectors
                    // if they point to the same direction - repel the incoming entity
                    var entityToOwner = entity.getPos().relativize(getPos());
                    var velocity = entity.getVelocity();
                    if (velocity.dotProduct(entityToOwner) >= 0) {
                        if (velocity.lengthSquared() >= 0.75 || entity instanceof ProjectileEntity) {
                            entity.setVelocity(entity.getVelocity().negate().multiply(0.5));
                            entity.velocityDirty = true;
                        } else {
                            entity.move(MovementType.PISTON, entityToOwner.negate());
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean canAvoidTraps() {
        return true;
    }

    public final void setVeilRadius(float width) {
        dataTracker.set(RADIUS, width);
    }

    public final float getVeilRadius() {
        return dataTracker.get(RADIUS);
    }

    public final float getVeilVisibleRadius() {
        return getVeilRadius() - SCREENS_OFFSET;
    }

    @Nullable
    @Override
    public PlayerEntity getOwner() {
        return owner;
    }

    private EntityDimensions getDimensions() {
        return EntityDimensions.changing(getVeilRadius() * 2, getVeilRadius() * 2);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return getDimensions();
    }

    @Override
    protected Box calculateBoundingBox() {
        return getDimensions().getBoxAt(getPos());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("radius", NbtElement.FLOAT_TYPE)) {
            setVeilRadius(nbt.getFloat("radius"));
        }

        if (nbt.contains("owner")) {
            Uuids.INT_STREAM_CODEC.decode(NbtOps.INSTANCE, nbt.get("owner"))
                    .resultOrPartial(Util.addPrefix("Could not decode a Player UUID of ", Wizcraft.LOGGER::error))
                    .ifPresent(pair -> owner = getEntityWorld().getPlayerByUuid(uuid));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("radius", getVeilRadius());
        if (owner != null) {
            Uuids.INT_STREAM_CODEC.encodeStart(NbtOps.INSTANCE, owner.getUuid()).ifSuccess(it -> nbt.put("owner", it));
        }
    }
}
