package com.tek.guardian.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class SpammingCache {
	
	private final int MAX_ENTRIES = 512;
	private Map<String, SpammingProfile> cache;
	
	@SuppressWarnings("serial")
	public SpammingCache() {
		cache = new LinkedHashMap<String, SpammingProfile>(MAX_ENTRIES + 1, .75f, true) {
			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<String, SpammingProfile> eldest) {
				return size() > MAX_ENTRIES;
			}
		};
	}
	
	public void cacheProfile(String userId, SpammingProfile profile) {
		cache.put(userId, profile);
	}
	
	public Optional<SpammingProfile> getCacheProfile(String userId) {
		if(cache.containsKey(userId)) return Optional.of(cache.get(userId));
		return Optional.empty();
	}
	
	public Map<String, SpammingProfile> getCache() {
		return cache;
	}
	
}