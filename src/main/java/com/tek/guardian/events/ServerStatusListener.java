package com.tek.guardian.events;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ServerStatusListener extends ListenerAdapter {
	
	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		Guild guild = event.getGuild();
		Guardian.getInstance().getMongoAdapter().createServerProfile(guild);
	}
	
	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		Guild guild = event.getGuild();
		Guardian.getInstance().getMongoAdapter().removeServerProfile(guild.getId());
	}
	
	@Override
	public void onGuildReady(GuildReadyEvent event) {
		ServerProfile profile = Guardian.getInstance().getMongoAdapter().getServerProfile(event.getGuild());
		profile.verifyInvites(event.getGuild());
	}
	
}
