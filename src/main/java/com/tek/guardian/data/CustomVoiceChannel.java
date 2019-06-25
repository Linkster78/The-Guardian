package com.tek.guardian.data;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.ChannelManager;

@Entity("custom_vc")
public class CustomVoiceChannel {
	
	@Id
	private String id;
	private String guildId;
	private String userId;
	private String channelId;
	private boolean joined;
	
	public CustomVoiceChannel() { }
	
	public CustomVoiceChannel(Category category, Member member) {
		this.id = category.getGuild().getId() + member.getId();
		this.guildId = category.getGuild().getId();
		this.userId = member.getId();
		this.joined = false;
	}
	
	public void create(Category category, Member member, int capacity, Runnable callback) {
		category.createVoiceChannel(member.getEffectiveName() + "'s Channel").queue(vc -> {
			ChannelManager manager = vc.getManager();
			manager.setUserLimit(capacity);
			manager.queue();
			this.channelId = vc.getId();
			callback.run();
		});
	}
	
	public String getId() {
		return id;
	}
	
	public String getGuildId() {
		return guildId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getChannelId() {
		return channelId;
	}
	
	public boolean isJoined() {
		return joined;
	}
	
	public void setJoined(boolean joined) {
		this.joined = joined;
	}
	
}
