package com.tek.guardian.timer;

import java.util.TimerTask;

import com.tek.guardian.data.CustomVoiceChannel;
import com.tek.guardian.main.Guardian;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class VoiceChannelChecker extends TimerTask {

	@Override
	public void run() {
		for(CustomVoiceChannel cvc : Guardian.getInstance().getMongoAdapter().getCustomVoiceChannels()) {
			JDA jda = Guardian.getInstance().getJDA();
			VoiceChannel vc = jda.getVoiceChannelById(cvc.getChannelId());
			if(vc == null) {
				Guardian.getInstance().getMongoAdapter().removeCustomVoiceChannel(cvc);
			} else {
				if(cvc.isJoined() && vc.getMembers().size() == 0) {
					Guardian.getInstance().getMongoAdapter().removeCustomVoiceChannel(cvc);
					vc.delete().queue();
				}
			}
		}
	}

}
