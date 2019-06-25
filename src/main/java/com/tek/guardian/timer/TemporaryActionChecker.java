package com.tek.guardian.timer;

import java.util.TimerTask;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.data.TemporaryAction;
import com.tek.guardian.main.Guardian;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class TemporaryActionChecker extends TimerTask {

	@Override
	public void run() {
		JDA jda = Guardian.getInstance().getJDA();
		for(TemporaryAction action : Guardian.getInstance().getMongoAdapter().getTemporaryActions()) {
			if(action.isDue()) {
				Guardian.getInstance().getMongoAdapter().removeTemporaryAction(action);
				Guild guild = jda.getGuildById(action.getGuildId());
				if(guild != null) {
					ServerProfile profile = Guardian.getInstance().getMongoAdapter().getServerProfile(guild);
					action.handle(profile, guild);
				}
			}
		}
	}

}
