package org.kybe;

import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.plugin.Plugin;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.Setting;

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
			Optional<IModule> optionalModule = RusherHackAPI.getModuleManager().getFeature("middleclick");
			if (!optionalModule.isPresent()) {
				ChatUtils.print("Module not found", Style.EMPTY.withColor(Color.red.getRGB()));
				return;
			}
			IModule module = optionalModule.get();
			module.registerSettings(new BooleanSetting("Windcharge", "Windcharge", false));
			module.getSetting("Windcharge").addSubSettings(
					new BooleanSetting("Boost Jump", "Boosts your jump", false)
			);
			module.getSetting("Windcharge").getSubSettings().get(0).addSubSettings(
					new BooleanSetting("Allow mid air Jump","Allow mid air Jump", false)
			);
			inputListener = new Listener();
			RusherHackAPI.getEventBus().subscribe(inputListener);
		} catch (Exception e) {
			ChatUtils.print("Error:" + e.getMessage(), Style.EMPTY.withColor(Color.red.getRGB()));
		}
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("Example plugin unloaded!");
	}
	
}