package com.mod.easyeat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class EasyEat implements ClientModInitializer {

    private boolean[] isQuickEating = new boolean[9];
    private boolean[] wasJustSelected = new boolean[9];  // Track newly selected slots

    @Override
    public void onInitializeClient() {
        System.out.println("EasyEat initialized!");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean anyQuickEating = false;

            for (int i = 0; i < 9; i++) {
                if (client.options.hotbarKeys[i].isPressed()) {
                    ItemStack stack = client.player.getInventory().getStack(i);
                    if (client.player.getMainHandStack().getItem().getComponents().contains(DataComponentTypes.FOOD)) {
                        if (!isQuickEating[i] && !wasJustSelected[i]) {
                            System.out.println("Starting to easy-eat food in slot " + (i+1));
                            client.player.getInventory().selectedSlot = i;
                            wasJustSelected[i] = true;  // Mark as just selected
                        } else if (!isQuickEating[i] && wasJustSelected[i]) {
                            // Start eating after one tick delay
                            isQuickEating[i] = true;
                            wasJustSelected[i] = false;
                            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
                        }
                        if (isQuickEating[i]) {
                            anyQuickEating = true;
                        }
                    }
                } else {
                    if (isQuickEating[i]) {
                        System.out.println("Stopped easy-eating from slot " + (i+1));
                        isQuickEating[i] = false;
                        wasJustSelected[i] = false;
                        client.options.useKey.setPressed(false);  // Reset use key when stopping
                    }
                }
            }

            // Only set use key to pressed if we're actively quick-eating
            if (anyQuickEating) {
                client.options.useKey.setPressed(true);
            }
        });
    }
}