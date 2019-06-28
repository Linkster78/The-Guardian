package com.tek.guardian.events;

import java.util.Optional;

import com.tek.guardian.data.RoleMemory;
import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleMemoryListener extends ListenerAdapter {
	
	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		ServerProfile profile = Guardian.getInstance().getMongoAdapter().getServerProfile(event.getGuild());
		
		if(profile.isSaveRoles()) {
			RoleMemory memory = new RoleMemory(event.getMember());
			Guardian.getInstance().getMongoAdapter().saveRoleMemory(memory);
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
	}
	
}
