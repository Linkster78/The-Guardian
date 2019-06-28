package com.tek.guardian.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import net.dv8tion.jda.api.entities.Message;

public class MessageCache {
	
	private final int MAX_ENTRIES = 1024;
	private Map<String, CachedMessage> cache;
	
	@SuppressWarnings("serial")
	public MessageCache() {
		cache = new LinkedHashMap<String, CachedMessage>(MAX_ENTRIES + 1, .75f, true) {
			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<String, CachedMessage> eldest) {
				return size() > MAX_ENTRIES;
			}
		};
	}
	
	public Optional<CachedMessage> getCachedMessage(String messageId) {
		if(cache.containsKey(messageId)) {
			Optional<CachedMessage> cachedMessageOpt = Optional.of(cache.get(messageId));
			cache.remove(messageId);
			return cachedMessageOpt;
		}
		return Optional.empty();
	}
	
	public void decache(String messageId) {
		if(cache.containsKey(messageId)) cache.remove(messageId);
	}
	
	public void cacheMessage(Message message) {
		cache.put(message.getId(), new CachedMessage(message));
	}
	
}
