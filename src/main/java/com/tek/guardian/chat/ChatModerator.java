package com.tek.guardian.chat;

import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ChatModerator {
	
	private List<ChatFilter> filters;
	
	public ChatModerator() {
		this.filters = new ArrayList<ChatFilter>();
	}
	
	public void passEvent(GuildMessageReceivedEvent event, ServerProfile profile) {
		if(!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
			for(ChatFilter filter : filters) {
				String reason = filter.filterChat(event.getMember().getUser(), event.getMessage().getContentStripped(), profile);
				if(reason != null) {
					event.getMessage().delete().queue();
					
					if(profile.getDeletedChannel() != null) {
						TextChannel channel = event.getGuild().getTextChannelById(profile.getDeletedChannel());
						if(channel != null) {
							User author = event.getMessage().getAuthor();
							DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
							
							MessageEmbed embed = Reference.formatEmbed(event.getJDA(), "Message Moderated")
									.setColor(Color.red)
									.addField("Creation Time", timeFormatter.format(event.getMessage().getTimeCreated()), true)
									.addField("Message ID", event.getMessageId(), true)
									.addField("Message Contents", event.getMessage().getContentRaw(), true)
									.addField("Deletion Reason", reason, true)
									.addField("Author", author.getName() + "#" + author.getDiscriminator(), true)
									.addField("Author ID", author.getId(), true)
									.build();
									
							channel.sendMessage(embed).queue();
						}
					}
					
					return;
				}
			}
		}
		
		Guardian.getInstance().getMessageCache().cacheMessage(event.getMessage());
	}
	
	public void registerFilter(ChatFilter filter) {
		filters.add(filter);
	}
	
	public List<ChatFilter> getFilters() {
		return filters;
	}
	
}
