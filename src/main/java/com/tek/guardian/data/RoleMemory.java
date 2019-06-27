package com.tek.guardian.data;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

@Entity("role_memory")
public class RoleMemory {
	
	@Id
	private ObjectId objectId;
	private String guildId;
	private String userId;
	private List<String> roles;
	
	public RoleMemory() { }
	
	public RoleMemory(Member member) {
		this.objectId = ObjectId.get();
		this.guildId = member.getGuild().getId();
		this.userId = member.getId();
		this.roles = member.getRoles().stream().filter(role -> member.getGuild().getSelfMember().canInteract(role)).map(Role::getId).collect(Collectors.toList());
	}
	
	public void apply(Member member) {
		Guild guild = member.getGuild();
		guild.modifyMemberRoles(member, roles.stream().map(guild::getRoleById)
				.filter(role -> role != null)
				.filter(guild.getSelfMember()::canInteract)
				.collect(Collectors.toList())).queue();
	}
	
	public ObjectId getObjectId() {
		return objectId;
	}
	
	public String getGuildId() {
		return guildId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public List<String> getRoles() {
		return roles;
	}
	
}
