package com.tek.guardian.events;

import java.util.Optional;

import com.tek.guardian.data.CustomVoiceChannel;
import com.tek.guardian.main.Guardian;

import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoiceChannelListener extends ListenerAdapter {
	
	@Override
	public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
		Optional<CustomVoiceChannel> cvcOpt = Guardian.getInstance().getMongoAdapter().getCustomVoiceChannel(event.getChannel().getId());
		if(cvcOpt.isPresent()) {
			Guardian.getInstance().getMongoAdapter().removeCustomVoiceChannel(cvcOpt.get());
		}
	}
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		Optional<CustomVoiceChannel> cvcOpt = Guardian.getInstance().getMongoAdapter().getCustomVoiceChannel(event.getChannelJoined().getId());
		if(cvcOpt.isPresent()) {
			if(event.getMember().getId().equals(cvcOpt.get().getUserId())) {
				cvcOpt.get().setJoined(true);
				Guardian.getInstance().getMongoAdapter().saveCustomVoiceChannel(cvcOpt.get());
			}
		}
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		Optional<CustomVoiceChannel> cvcOpt = Guardian.getInstance().getMongoAdapter().getCustomVoiceChannel(event.getChannelLeft().getId());
		if(cvcOpt.isPresent()) {
			if(cvcOpt.get().isJoined()) {
				if(event.getChannelLeft().getMembers().size() == 0) {
					Guardian.getInstance().getMongoAdapter().removeCustomVoiceChannel(cvcOpt.get());
					event.getChannelLeft().delete().queue();
				}
			}
		}
	}
	
}
