package com.tek.guardian.data;

import org.bson.types.ObjectId;

import com.tek.guardian.enums.Action;
import com.tek.guardian.enums.BotRole;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

@Entity("temporary_actions")
public class TemporaryAction {
	
	@Id
	private ObjectId objectId;
	private String userId;
	private String guildId;
	private Action action;
	private long assigned;
	private long period;
	
	public TemporaryAction() { }
	
	public TemporaryAction(String userId, String guildId, Action action, long assigned, long period) {
		this.objectId = new ObjectId();;
		this.userId = userId;
		this.guildId = guildId;
		this.action = action;
		this.assigned = assigned;
		this.period = period;
	}

	public void handle(ServerProfile profile, Guild guild) {
		Member member = guild.getMemberById(userId);
		
		if(action.equals(Action.TEMPMUTE)) {
			if(member == null) return;
			
			Role muted = guild.getRoleById(profile.getRoleMap().get(BotRole.MUTED.name()));
			if(member.getRoles().contains(muted)) {
				guild.removeRoleFromMember(member, muted).queue();
			}
		} else if(action.equals(Action.TEMPDEAFEN)) {
			if(member == null) return;
			
			if(member.getVoiceState().isGuildDeafened()) {
				member.deafen(false).queue();
			}
		} else {
			guild.retrieveBanList().queue(bans -> {
				bans.stream().filter(ban -> ban.getUser().getId().equals(userId)).forEach(ban -> guild.unban(ban.getUser()));
			});
		}
	}
	
	public boolean isDue() {
		return System.currentTimeMillis() >= assigned + period;
	}
	
	public ObjectId getObjectId() {
		return objectId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getGuildId() {
		return guildId;
	}
	
	public Action getAction() {
		return action;
	}
	
	public long getAssigned() {
		return assigned;
	}
	
	public long getPeriod() {
		return period;
	}
	
}
