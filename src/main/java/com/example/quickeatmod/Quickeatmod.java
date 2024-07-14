package com.example.quickeatmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class Quickeatmod implements ClientModInitializer {

    private boolean[] isQuickEating = new boolean[9];

    @Override
    public void onInitializeClient() {
        System.out.println("QuickEatMod initialized!");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean anyQuickEating = false;

            for (int i = 0; i < 9; i++) {
                if (client.options.hotbarKeys[i].isPressed()) {
                    ItemStack stack = client.player.getInventory().getStack(i);
                    if (stack.isFood()) {
                        if (!isQuickEating[i]) {
                            System.out.println("Starting to quick-eat food in slot " + (i+1));
                            client.player.getInventory().selectedSlot = i;
                            isQuickEating[i] = true;
                            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
                        }
                        anyQuickEating = true;
                    }
                } else {
                    if (isQuickEating[i]) {
                        System.out.println("Stopped quick-eating from slot " + (i+1));
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
