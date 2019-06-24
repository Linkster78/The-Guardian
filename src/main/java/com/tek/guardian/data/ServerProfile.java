package com.tek.guardian.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tek.guardian.main.Guardian;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import net.dv8tion.jda.api.entities.Guild;

@Entity("server_profiles")
public class ServerProfile {
	
	@Id
	private String serverId;
	private String prefix;
	private boolean deleteCommands;
	private boolean replyUnknown;
	private List<String> commandChannels;
	private List<String> lockedChannels;
	private Map<String, String> roleMap;
	
	public ServerProfile() {
		this.commandChannels = Arrays.asList();
		this.lockedChannels = Arrays.asList();
		this.roleMap = new HashMap<String, String>();
	}
	
	public ServerProfile(String serverId) {
		this.serverId = serverId;
		this.prefix = Guardian.getInstance().getConfig().getDefaultPrefix();
		this.deleteCommands = true;
		this.replyUnknown = true;
		this.commandChannels = Arrays.asList();
		this.lockedChannels = Arrays.asList();
		this.roleMap = new HashMap<String, String>();
	}
	
	public void join(Guild guild) { }
	
	public void leave(Guild guild) { }
	
	public boolean canSendCommand(String channelId) {
		if(commandChannels.isEmpty()) return true;
		return commandChannels.contains(channelId);
	}
	
	public boolean isLocked(String channelId) {
		return lockedChannels.contains(channelId);
	}
	
	public String getServerId() {
		return serverId;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public boolean doesDeleteCommands() {
		return deleteCommands;
	}
	
	public boolean doesReplyUnknown() {
		return replyUnknown;
	}
	
	public List<String> getCommandChannels() {
		return commandChannels;
	}
	
	public List<String> getLockedChannels() {
		return lockedChannels;
	}
	
	public Map<String, String> getRoleMap() {
		return roleMap;
	}
	
}
