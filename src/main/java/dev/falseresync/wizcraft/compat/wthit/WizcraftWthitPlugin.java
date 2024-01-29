package dev.falseresync.wizcraft.compat.wthit;

import dev.falseresync.libhudcompat.LibHudCompat;
import dev.falseresync.wizcraft.api.common.worktable.WorktableBlock;
import dev.falseresync.wizcraft.common.block.WizcraftBlocks;
import mcp.mobius.waila.api.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class WizcraftWthitPlugin implements IWailaPlugin, IEventListener {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addEventListener(this);
        registrar.addOverride(new IBlockComponentProvider() {
            @Override
            public @Nullable BlockState getOverride(IBlockAccessor accessor, IPluginConfig config) {
                return WizcraftBlocks.DUMMY_WORKTABLE.getDefaultState();
            }
        }, WorktableBlock.class);
    }

    @Override
    public void onBeforeTooltipRender(DrawContext ctx, Rectangle rect, ICommonAccessor accessor, IPluginConfig config, Canceller canceller) {
        if (!LibHudCompat.isRegionFree(rect.x, rect.y, rect.width, rect.height)) {
            canceller.cancel();
        }
    }
}
