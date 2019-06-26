package com.tek.guardian.events;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LockedListener {
	
	public static boolean onGuildMessageReceived(ServerProfile profile, GuildMessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return false;
		
		Member member = event.getMember();
		TextChannel channel = event.getChannel();
		
		if(profile.isLocked(channel.getId())) {
			if(!profile.canMessage(channel.getId(), member)) {
				event.getMessage().delete().queue();
				return true;
			} else {
				Guardian.getInstance().getMessageCache().cacheMessage(event.getMessage());
			}
		} else {
			Guardian.getInstance().getMessageCache().cacheMessage(event.getMessage());
		}
		
		return false;
	}
	
}
