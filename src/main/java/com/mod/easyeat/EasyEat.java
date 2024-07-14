package com.mod.easyeat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class EasyEat implements ClientModInitializer {

    private boolean[] isQuickEating = new boolean[9];

    @Override
    public void onInitializeClient() {
        System.out.println("EasyEat initialized!");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean anyQuickEating = false;

            for (int i = 0; i < 9; i++) {
                if (client.options.hotbarKeys[i].isPressed()) {
                    ItemStack stack = client.player.getInventory().getStack(i);
                    if (stack.isFood()) {
                        if (!isQuickEating[i]) {
                            System.out.println("Starting to easy-eat food in slot " + (i+1));
                            client.player.getInventory().selectedSlot = i;
                            isQuickEating[i] = true;
                            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
                        }
                        anyQuickEating = true;
                    }
                } else {
                    if (isQuickEating[i]) {
                        System.out.println("Stopped easy-eating from slot " + (i+1));
                        isQuickEating[i] = false;
                    }
                }
            }

            // Only override use key if we're quick-eating
            if (anyQuickEating) {
                client.options.useKey.setPressed(true);
            } else {
                // Reset the use key to its actual state
                boolean rightMousePressed = MinecraftClient.getInstance().mouse.wasRightButtonClicked();
                client.options.useKey.setPressed(rightMousePressed);
            }
        });
    }
}
