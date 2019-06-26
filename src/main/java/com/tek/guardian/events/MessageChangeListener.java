package com.tek.guardian.events;

import java.awt.Color;
import java.util.Optional;

import com.tek.guardian.cache.CachedMessage;
import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageChangeListener extends ListenerAdapter {
	
	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
		ServerProfile profile = Guardian.getInstance().getMongoAdapter().getServerProfile(event.getGuild());
		
		if(profile.getDeletedChannel() != null) {
			TextChannel channel = event.getGuild().getTextChannelById(profile.getDeletedChannel());
			if(channel != null) {
				Optional<CachedMessage> messageOpt = Guardian.getInstance().getMessageCache().getCachedMessage(event.getMessageId());
				if(messageOpt.isPresent()) {
					Member author = event.getGuild().getMemberById(messageOpt.get().getAuthorId());
					
					if(!author.getUser().isBot()) {
						MessageEmbed embed = Reference.formatEmbed(event.getJDA(), "Message Deleted")
								.setColor(Color.red)
								.addField("Creation Time", messageOpt.get().getFormattedTime(), true)
								.addField("Message ID", event.getMessageId(), true)
								.addField("Message Contents", messageOpt.get().getContentRaw(), true)
								.addField("Author", author == null ? "Unknown Author" : author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
								.addField("Author ID", author == null ? "Unknown Author" : author.getId(), true)
								.build();
						
						channel.sendMessage(embed).queue();
					}
				}
			}
		}
	}
	
	@Override
	public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
		ServerProfile profile = Guardian.getInstance().getMongoAdapter().getServerProfile(event.getGuild());
		
		if(profile.getDeletedChannel() != null) {
			TextChannel channel = event.getGuild().getTextChannelById(profile.getDeletedChannel());
			if(channel != null) {
				Optional<CachedMessage> messageOpt = Guardian.getInstance().getMessageCache().getCachedMessage(event.getMessageId());
				if(messageOpt.isPresent()) {
					Member author = event.getGuild().getMemberById(messageOpt.get().getAuthorId());
					
					if(!author.getUser().isBot()) {
						Guardian.getInstance().getMessageCache().cacheMessage(event.getMessage());
						
						MessageEmbed embed = Reference.formatEmbed(event.getJDA(), "Message Edited")
								.setColor(Color.yellow)
								.addField("Creation Time", messageOpt.get().getFormattedTime(), true)
								.addField("Message ID", event.getMessageId(), true)
								.addField("Old Message Contents", messageOpt.get().getContentRaw(), true)
								.addField("New Message Contents", event.getMessage().getContentRaw(), true)
								.addField("Author", author == null ? "Unknown Author" : author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
								.addField("Author ID", author == null ? "Unknown Author" : author.getId(), true)
								.build();
						
						channel.sendMessage(embed).queue();
					}
				}
			}
		}
	}
	
}
