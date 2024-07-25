package org.kybe;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Items;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.client.input.EventMouse;
import org.rusherhack.client.api.events.network.EventPacket;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.event.stage.Stage;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.setting.BooleanSetting;

import java.awt.*;
import java.util.EventListener;
import java.util.Optional;

public class Listener implements EventListener {
	Minecraft mc = Minecraft.getInstance();

	@Subscribe(stage = Stage.PRE)
	private void onMouseInput(EventMouse.Key event) {
		try {
			if (event.getButton() != 2) return;
			Optional<IModule> optionalModule = RusherHackAPI.getModuleManager().getFeature("middleclick");
			if (optionalModule.isEmpty()) {
				ChatUtils.print("Module not found", Style.EMPTY.withColor(Color.red.getRGB()));
				return;
			}
			IModule module = optionalModule.get();
			BooleanSetting active = (BooleanSetting) module.getSetting("Windcharge");
			BooleanSetting boostJump = (BooleanSetting) module.getSetting("Windcharge").getSubSettings().get(0);
			BooleanSetting boostJumpmMidAir = (BooleanSetting) module.getSetting("Windcharge").getSubSettings().get(0).getSubSettings().get(0);

			if (mc.player == null || mc.gameMode == null || !active.getValue()) return;
			int slot = mc.player.getInventory().findSlotMatchingItem(Items.WIND_CHARGE.getDefaultInstance());
			int currentSlot = mc.player.getInventory().selected;

			boolean itemInHotbar = slot >= 0 && slot <= 8;

			if (itemInHotbar) {
				mc.player.getInventory().selected = slot;
				mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
				mc.player.getInventory().selected = currentSlot;
			} else if (slot != -1) {
				mc.setScreen(new net.minecraft.client.gui.screens.inventory.InventoryScreen(mc.player));
				mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, slot, currentSlot, ClickType.SWAP, mc.player);
				mc.setScreen(null);
				mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
				mc.setScreen(new net.minecraft.client.gui.screens.inventory.InventoryScreen(mc.player));
				mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, slot, currentSlot, ClickType.SWAP, mc.player);
				mc.setScreen(null);
			} else {
				ChatUtils.print("No swap performed. Either item not found or already in selected slot.");
			}

			if (boostJump.getValue()) {
				if (boostJumpmMidAir.getValue()) {
					mc.player.jumpFromGround();
				} else {
					if (mc.player.onGround()) {
						mc.player.jumpFromGround();
					}
				}
			}

		} catch (Exception e){
			ChatUtils.print("Error:" + e.getMessage(), Style.EMPTY.withColor(Color.red.getRGB()));
		}
	}

	@Subscribe
	private void onJump(EventPacket.Send event) {

	}
}
