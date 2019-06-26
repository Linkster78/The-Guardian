package com.tek.guardian.cache;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import net.dv8tion.jda.api.entities.Message;

public class CachedMessage {
	
	private String contentRaw;
	private OffsetDateTime timeCreated;
	private String authorId;
	
	public CachedMessage(Message message) {
		this.contentRaw = message.getContentRaw();
		this.timeCreated = message.getTimeCreated();
		this.authorId = message.getAuthor().getId();
	}
	
	public String getContentRaw() {
		return contentRaw;
	}
	
	public OffsetDateTime getTimeCreated() {
		return timeCreated;
	}
	
	public String getFormattedTime() {
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return timeFormatter.format(timeCreated);
	}
	
	public String getAuthorId() {
		return authorId;
	}
	
}
