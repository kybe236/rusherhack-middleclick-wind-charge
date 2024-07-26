package org.kybe;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.client.input.EventMouse;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.setting.BooleanSetting;

import java.awt.*;
import java.util.EventListener;
import java.util.Optional;

/**
 * Listenes for middleclick and executes actions
 * with Windcharges.
 *
 * @author kybe236
 */
public class Listener implements EventListener {
	private static final int MIDDLE_MOUSE_BUTTON = 2;
	private static final int INVENTORY_HOTBAR_SIZE = 8;

	Minecraft mc = Minecraft.getInstance();

	/*
	 * the eventmouse handler
	 */
	@Subscribe
	private void onMouseInput(EventMouse.Key event) {
		if (isMiddleClickPress(event)) {
			handleMiddleClick();
		}
	}

	/*
	 * Check if its middleclick
	 */
	private boolean isMiddleClickPress(EventMouse.Key event) {
		return event.getButton() == MIDDLE_MOUSE_BUTTON && event.getAction() == GLFW.GLFW_PRESS;
	}

	/*
	 * Main handler function for middleclick
	 */
	private void handleMiddleClick() {
		try {
			Optional<IModule> optionalModule = RusherHackAPI.getModuleManager().getFeature("middleclick");
			if (optionalModule.isEmpty()) {
				ChatUtils.print("Module not found", Style.EMPTY.withColor(Color.red.getRGB()));
				return;
			}

			ToggleableModule module = (ToggleableModule) optionalModule.get();
			if (!module.isToggled()) return;

			BooleanSetting active = (BooleanSetting) module.getSetting("Windcharge");
			BooleanSetting boostJump = (BooleanSetting) active.getSubSettings().get(0);
			BooleanSetting boostJumpMidAir = (BooleanSetting) boostJump.getSubSettings().get(0);

			if (mc.player == null || mc.gameMode == null || !active.getValue()) return;

			int slot = mc.player.getInventory().findSlotMatchingItem(Items.WIND_CHARGE.getDefaultInstance());
			int currentSlot = mc.player.getInventory().selected;

			if (useWindChargeItem(slot, currentSlot)) {
				handleBoostJump(boostJump, boostJumpMidAir);
			}

		} catch (Exception e) {
			ChatUtils.print("Error: " + e.getMessage(), Style.EMPTY.withColor(Color.red.getRGB()));
		}
	}

	/*
	 * throws an windcharge
	 */
	private boolean useWindChargeItem(int slot, int currentSlot) {
		boolean itemInHotbar = slot >= 0 && slot <= INVENTORY_HOTBAR_SIZE;

		if (itemInHotbar) {
			mc.player.getInventory().selected = slot;
			mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
			mc.player.getInventory().selected = currentSlot;
		} else if (slot != -1) {
			swapAndUseItem(slot, currentSlot);
		} else {
			return false;
		}

		return true;
	}

	/*
	 * swap from slot to currentSlot
	 */
	private void swapAndUseItem(int slot, int currentSlot) {
		mc.setScreen(new net.minecraft.client.gui.screens.inventory.InventoryScreen(mc.player));
		mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, slot, currentSlot, ClickType.SWAP, mc.player);
		mc.setScreen(null);
		mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
		mc.setScreen(new net.minecraft.client.gui.screens.inventory.InventoryScreen(mc.player));
		mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, slot, currentSlot, ClickType.SWAP, mc.player);
		mc.setScreen(null);
	}

	/*
	 * Check if the player is allowed to jump
	 */
	private void handleBoostJump(BooleanSetting boostJump, BooleanSetting boostJumpMidAir) {
		if (boostJump.getValue()) {
			if (!(boostJumpMidAir.getValue() && mc.player.onGround()) | boostJumpMidAir.getValue()) {
				mc.player.jumpFromGround();
			}
		}
	}
}