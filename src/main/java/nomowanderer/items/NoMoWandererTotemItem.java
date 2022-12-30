package nomowanderer.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nomowanderer.Registry;
import nomowanderer.compat.ExternalMods;
import nomowanderer.util.HoverTextUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class NoMoWandererTotemItem extends Item {

    public static final String ID = "no_mo_wanderer_totem";

    public NoMoWandererTotemItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack defaultInstance = super.getDefaultInstance();
        CompoundTag enabled = defaultInstance.getOrCreateTag();
        enabled.putBoolean("Enabled", true);
        return defaultInstance;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        if (player.isShiftKeyDown() && !level.isClientSide()) {
            ItemStack item = player.getItemInHand(hand);
            CompoundTag tag = item.getOrCreateTag();
            tag.putBoolean("Enabled", !tag.getBoolean("Enabled"));
            return InteractionResultHolder.pass(item);
        }
        return super.use(level, player, hand);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> toolTips, @NotNull TooltipFlag flag) {
        addEnabledTooltip(stack, toolTips);
        if (Screen.hasShiftDown()) {
            HoverTextUtil.addCommonText(toolTips);
            String totemMessage = String.format(
                    "Can be anywhere in your inventory%s.", ExternalMods.CURIOS.isLoaded() ? " or a Curios slot" : ""
            );
            toolTips.add(
                    Component.literal(totemMessage).withStyle(ChatFormatting.YELLOW)
            );
            toolTips.add(
                    Component.literal("Sneak right-click ").withStyle(ChatFormatting.GOLD)
                            .append(Component.literal("to toggle on/off").withStyle(ChatFormatting.GRAY))
            );
        } else {
            toolTips.add(
                    Component.literal("Hold ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal("SHIFT ").withStyle(ChatFormatting.AQUA))
                        .append(Component.literal("for details.").withStyle(ChatFormatting.GRAY))
            );
        }
        super.appendHoverText(stack, level, toolTips, flag);
    }

    private static void addEnabledTooltip(@NotNull ItemStack stack, @NotNull List<Component> toolTips) {
        boolean enabled = NoMoWandererTotemItem.isEnabled(stack);
        toolTips.add(
                Component.literal("Enabled: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(enabled ? "Yes" : "No").withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED))
        );
    }

    public static boolean isEnabled(ItemStack stack) {
        if (!stack.getItem().equals(Registry.NO_SOLICITING_TALISMAN.get())) {
            return false;
        }
        // If there are no tags, assume it is enabled (for backwards compatability)
        if (stack.getTag() == null) {
            return true;
        }
        return stack.getTag().getBoolean("Enabled");
    }

}
