package com.tek.guardian.data;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity("reaction_roles")
public class ReactionRole {
	
	@Id
	private ObjectId objectId;
	private String guildId;
	private String messageId;
	private String emoteId;
	private String roleId;
	private boolean emoji;
	
	public ReactionRole() { 
		this.emoji = false;
	}

	public ReactionRole(String guildId, String messageId, String emoteId, String roleId, boolean emoji) {
		this.objectId = ObjectId.get();
		this.guildId = guildId;
		this.messageId = messageId;
		this.emoteId = emoteId;
		this.roleId = roleId;
		this.emoji = emoji;
	}
	
	public ObjectId getObjectId() {
		return objectId;
	}
	
	public String getGuildId() {
		return guildId;
	}
	
	public String getMessageId() {
		return messageId;
	}
	
	public String getEmoteId() {
		return emoteId;
	}
	
	public String getRoleId() {
		return roleId;
	}
	
	public boolean isEmoji() {
		return emoji;
	}
	
}
