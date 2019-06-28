package com.tek.guardian.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import com.tek.guardian.chat.SpammingFilter;

public class SpammingProfile {
	
	private final double SIMILARITY_THRESHOLD = 0.8;
	private final int MAX_ENTRIES = 5;
	private Map<String, Integer> messageCache;
	
	@SuppressWarnings("serial")
	public SpammingProfile() {
		messageCache = new LinkedHashMap<String, Integer>(MAX_ENTRIES + 1, .75f, true) {
			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<String, Integer> eldest) {
				return size() > MAX_ENTRIES;
			}
		};
	}
	
	public String getSimilar(String str) {
		for(String key : messageCache.keySet()) {
			if(SpammingFilter.calculateStringSimilarity(str.toLowerCase(), key.toLowerCase()) >= SIMILARITY_THRESHOLD) {
				return key;
			}
		}
		
		return null;
	}
	
	public void cacheString(String message) {
		messageCache.put(message, 1);
	}
	
	public void incrementString(String message) {
		messageCache.put(message, messageCache.get(message) + 1);
	}
	
	public int getCount(String message) {
		return messageCache.get(message);
	}
	
	public Map<String, Integer> getMessageCache() {
		return messageCache;
	}
	
}