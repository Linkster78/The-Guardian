package com.tek.guardian.timer;

import java.util.TimerTask;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;

import net.dv8tion.jda.api.entities.Guild;

public class ServerProfileChecker extends TimerTask {

	@Override
	public void run() {
		for(ServerProfile profile : Guardian.getInstance().getMongoAdapter().getServerProfiles()) {
			Guild guild = Guardian.getInstance().getJDA().getGuildById(profile.getServerId());
			if(guild == null) {
				Guardian.getInstance().getMongoAdapter().removeServerProfile(profile.getServerId());
			}
		}
	}

}
