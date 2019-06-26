package com.tek.guardian.events;

import java.util.List;

import com.tek.guardian.data.ReactionRole;
import com.tek.guardian.main.Guardian;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionRoleListener extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		if(event.getMember().getUser().isBot()) return;
		
		List<ReactionRole> roles = Guardian.getInstance().getMongoAdapter().getReactionRoles(event.getMessageId());
		if(!roles.isEmpty()) {
			for(ReactionRole role : roles) {
				if(role.isEmoji() && event.getReactionEmote().isEmoji() && role.getEmoteId().equals(event.getReactionEmote().getEmoji())) {
					addRole(event.getMember(), event.getGuild(), role);
				} else if(!role.isEmoji() && event.getReactionEmote().isEmote() && role.getEmoteId().equals(event.getReactionEmote().getEmote().getId())) {
					addRole(event.getMember(), event.getGuild(), role);
				}
			}
		}
	}
	
	@Override
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
		if(event.getMember().getUser().isBot()) return;
		
		List<ReactionRole> roles = Guardian.getInstance().getMongoAdapter().getReactionRoles(event.getMessageId());
		if(!roles.isEmpty()) {
			for(ReactionRole role : roles) {
				if(role.isEmoji() && event.getReactionEmote().isEmoji() && role.getEmoteId().equals(event.getReactionEmote().getEmoji())) {
					removeRole(event.getMember(), event.getGuild(), role);
				} else if(!role.isEmoji() && event.getReactionEmote().isEmote() && role.getEmoteId().equals(event.getReactionEmote().getEmote().getId())) {
					removeRole(event.getMember(), event.getGuild(), role);
				}
			}
		}
	}
	
	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
		List<ReactionRole> roles = Guardian.getInstance().getMongoAdapter().getReactionRoles(event.getMessageId());
		for(ReactionRole role : roles) {
			Guardian.getInstance().getMongoAdapter().removeReactionRole(role);
		}
	}
	
	public void addRole(Member member, Guild guild, ReactionRole reactionRole) {
		Role role = guild.getRoleById(reactionRole.getRoleId());
		if(role != null) {
			if(guild.getSelfMember().canInteract(role)) {
				guild.addRoleToMember(member, role).queue();
			}
		} else {
			Guardian.getInstance().getMongoAdapter().removeReactionRole(reactionRole);
		}
	}
	
	public void removeRole(Member member, Guild guild, ReactionRole reactionRole) {
		Role role = guild.getRoleById(reactionRole.getRoleId());
		if(role != null) {
			if(guild.getSelfMember().canInteract(role)) {
				guild.removeRoleFromMember(member, role).queue();
			}
		} else {
			Guardian.getInstance().getMongoAdapter().removeReactionRole(reactionRole);
		}
	}
	
}
