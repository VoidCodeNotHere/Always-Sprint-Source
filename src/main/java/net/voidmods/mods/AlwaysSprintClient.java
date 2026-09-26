package net.voidmods.mods;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;

public class AlwaysSprintClient implements ClientModInitializer {

    public static boolean isModEnabled = true;
    private static KeyMapping togglekey;

    @Override
    public void onInitializeClient() {


        togglekey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "Always Sprint Mod",
                InputConstants.Type.KEYSYM,
                -1,
                KeyMapping.Category.MOVEMENT
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Keybind section
            while (togglekey.consumeClick()) {
                isModEnabled = !isModEnabled;

                if (client.player != null) {
                    String state = isModEnabled ? "§l§aEnabled" : "§l§cDisabled";
                    client.player.sendSystemMessage(Component.literal("Always Sprint: " + state));
                }
            }
            // checks if mod enabled
            if (!isModEnabled) return;
            // checks if player exist or not to insure we process valid states
            if (client.player == null) return;
            // checks if player is holding forward moving key or not
            if (!client.options.keyUp.isDown()) return;
            // checks if player doing any vanilla mechanics that prevent sprinting
            if (client.player.isCrouching() || client.player.isUsingItem() || client.player.isPassenger()) return;
            // checks that if player is already sprinting
            if (client.player.isSprinting()) return;

            // checks that player is not hungry or has blindness or slowness effects to prevent glitches
            boolean hasEnoughFood = client.player.getFoodData().getFoodLevel() > 6 || client.player.isCreative();
            boolean isNotBlind = !client.player.hasEffect(MobEffects.BLINDNESS);
            boolean isNotSlow = !client.player.hasEffect(MobEffects.SLOWNESS);

                if (hasEnoughFood && isNotBlind && isNotSlow) {
                    client.player.setSprinting(true);
                }
        });
    }
}
