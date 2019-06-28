package com.tek.guardian.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.tek.guardian.data.ServerProfile;

public class ServerProfileCache {
	
	private final int MAX_ENTRIES = 32;
	private Map<String, ServerProfile> cache;
	
	@SuppressWarnings("serial")
	public ServerProfileCache() {
		cache = new LinkedHashMap<String, ServerProfile>(MAX_ENTRIES + 1, .75f, true) {
			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<String, ServerProfile> eldest) {
				return size() > MAX_ENTRIES;
			}
		};
	}
	
	public void cacheServerProfile(String guildId, ServerProfile profile) {
		cache.put(guildId, profile);
	}
	
	public Optional<ServerProfile> getServerProfile(String guildId) {
		if(cache.containsKey(guildId)) return Optional.of(cache.get(guildId));
		return Optional.empty();
	}
	
}
