package com.tek.guardian.events;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReceivedListener extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Member member = event.getMember();
		Guild guild = event.getGuild();
		TextChannel channel = event.getChannel();
		ServerProfile profile = Guardian.getInstance().getMongoAdapter().getServerProfile(guild);
		
		if(profile.isLocked(channel.getId())) {
			if(!profile.canMessage(channel.getId(), member)) {
				event.getMessage().delete().queue();
			}
		}
	}
	
}
