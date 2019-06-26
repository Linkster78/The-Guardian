package com.tek.guardian.data;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity("reaction_roles")
public class ReactionRole {
	
	@Id
	private ObjectId objectId;
	private String messageId;
	private String emoteId;
	private String roleId;
	private boolean emoji;
	
	public ReactionRole() { 
		this.emoji = false;
	}

	public ReactionRole(String messageId, String emoteId, String roleId, boolean emoji) {
		this.objectId = ObjectId.get();
		this.messageId = messageId;
		this.emoteId = emoteId;
		this.roleId = roleId;
		this.emoji = emoji;
	}
	
	public ObjectId getObjectId() {
		return objectId;
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
