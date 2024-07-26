package org.kybe;

import net.minecraft.network.chat.Style;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.plugin.Plugin;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.setting.BooleanSetting;

import java.awt.*;
import java.util.EventListener;
import java.util.Optional;

/**
 * Middleclick Windcharge plugin
 *
 * @author kybe236
 */
public class MiddleclickWindcharge extends Plugin {
	private EventListener inputListener;
	
	@Override
	public void onLoad() {
		try {
			/*
			 * Log the load event
			 */
			this.getLogger().info("[MIDDLECLICKWINDCHARGE] loaded");

			/*
			 * Get the middleclik module and check its presence
			 */
			Optional<IModule> optionalModule = RusherHackAPI.getModuleManager().getFeature("middleclick");
			if (!optionalModule.isPresent()) {
				ChatUtils.print("Module not found", Style.EMPTY.withColor(Color.red.getRGB()));
				return;
			}
			IModule module = optionalModule.get();

			/*
			 * Register settings
			 * Boost Jump: if the player should jump
			 * Boost Jump>Allow mid air Jump: if the player is allowed to Airjump
			 */
			module.registerSettings(new BooleanSetting("Windcharge", "Windcharge", false));
			module.getSetting("Windcharge").addSubSettings(
					new BooleanSetting("Boost Jump", "Boosts your jump", false)
			);
			module.getSetting("Windcharge").getSubSettings().get(0).addSubSettings(
					new BooleanSetting("Allow mid air Jump","Allow mid air Jump", false)
			);

			/*
			 * Register the Listener
			 */
			inputListener = new Listener();
			RusherHackAPI.getEventBus().subscribe(inputListener);
		} catch (Exception e) {
			/*
			 * Print errors if they happen
			 */
			ChatUtils.print("Error:" + e.getMessage(), Style.EMPTY.withColor(Color.red.getRGB()));
		}
	}
	
	@Override
	public void onUnload() {
		/*
		 * Log the Unload event
		 */
		this.getLogger().info("[MIDDLECLICKWINDCHARGE] unloaded");
	}
}