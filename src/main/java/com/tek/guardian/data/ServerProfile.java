package com.tek.guardian.data;

import java.util.List;
import java.util.Map;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity("server_profiles")
public class ServerProfile {
	
	@Id
	private String serverId;
	private String prefix;
	private List<String> commandChannels;
	private List<String> lockedChannels;
	private Map<String, String> roleMap;
	
}
