package com.tek.guardian.events;

import java.awt.Color;
import java.util.Optional;

import com.tek.guardian.data.RoleMemory;
import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberStatusListener extends ListenerAdapter {
	
	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		ServerProfile profile = Guardian.getInstance().getMongoAdapter().getServerProfile(event.getGuild());
		
		if(profile.isSaveRoles()) {
			RoleMemory memory = new RoleMemory(event.getMember());
			Guardian.getInstance().getMongoAdapter().saveRoleMemory(memory);
		}
		
		if(profile.getLogChannel() != null) {
			TextChannel log = event.getGuild().getTextChannelById(profile.getLogChannel());
			if(log != null) {
				MessageEmbed embed = Reference.formatEmbed(event.getJDA(), "User Left")
						.setColor(Color.cyan)
						.setDescription("A user left the server.")
						.setThumbnail(event.getUser().getEffectiveAvatarUrl())
						.addField("User Name", event.getUser().getName() + "#" + event.getUser().getDiscriminator(), true)
						.addField("User ID", event.getUser().getId(), true)
						.build();
				
				log.sendMessage(embed).queue();
			}
		}
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		ServerProfile profile = Guardian.getInstance().getMongoAdapter().getServerProfile(event.getGuild());
		
		if(profile.isSaveRoles()) {
			Optional<RoleMemory> memoryOpt = Guardian.getInstance().getMongoAdapter().getRoleMemory(event.getGuild().getId(), event.getUser().getId());
			if(memoryOpt.isPresent()) {
				memoryOpt.get().apply(event.getMember());
				Guardian.getInstance().getMongoAdapter().removeRoleMemory(memoryOpt.get());
			}
		}
		
		if(profile.getJoinRole() != null) {
			Role joinRole = event.getGuild().getRoleById(profile.getJoinRole());
			if(joinRole != null) {
				if(event.getGuild().getSelfMember().canInteract(joinRole)) {
					if(!event.getMember().getRoles().contains(joinRole)) {
						event.getGuild().addRoleToMember(event.getMember(), joinRole).queue();
					}
				}
			} else {
				profile.setJoinRole(null);
				profile.save();
			}
		}
		
		if(profile.getLogChannel() != null) {
			TextChannel log = event.getGuild().getTextChannelById(profile.getLogChannel());
			if(log != null) {
				Optional<String> inviteUsed = profile.resolveInvite(event.getGuild());
				Optional<Integer> inviteCount = inviteUsed.isPresent() ? Optional.of(profile.getInviteMap().get(inviteUsed.get())) : Optional.empty();
				
				MessageEmbed embed = Reference.formatEmbed(event.getJDA(), "User Joined")
						.setColor(Color.cyan)
						.setDescription("A user joined the server.")
						.setThumbnail(event.getUser().getEffectiveAvatarUrl())
						.addField("User Name", event.getUser().getName() + "#" + event.getUser().getDiscriminator(), true)
						.addField("User ID", event.getUser().getId(), true)
						.addField("Invite Used", inviteUsed.isPresent() ? inviteUsed.get() : "Couldn't Resolve", true)
						.addField("Invite Uses", inviteCount.isPresent() ? Integer.toString(inviteCount.get()) : "Couldn't Resolve", true)
						.build();
				
				log.sendMessage(embed).queue();
			}
		}
	}
	
}
