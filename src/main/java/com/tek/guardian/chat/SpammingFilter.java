package com.tek.guardian.chat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.tek.guardian.data.ServerProfile;

import net.dv8tion.jda.api.entities.User;
import net.ricecode.similarity.LevenshteinDistanceStrategy;

public class SpammingFilter implements ChatFilter {

	private final int SPAM_THRESHOLD = 3;
	private SpammingCache cache;
	
	public SpammingFilter() {
		this.cache = new SpammingCache();
	}
	
	@Override
	public String filterChat(User user, String message, ServerProfile profile) {
		if(!profile.isModerateSpam()) return null;
		
		SpammingProfile sprofile;
		Optional<SpammingProfile> profileOpt = cache.getCacheProfile(user.getId());
		if(profileOpt.isPresent()) {
			sprofile = profileOpt.get();
		} else {
			sprofile = new SpammingProfile();
		}
		
		String similar;
		if((similar = sprofile.getSimilar(message)) != null) {
			sprofile.incrementString(similar);
			int count = sprofile.getCount(similar);
			if(count >= SPAM_THRESHOLD) {
				return "This message was sent similarly " + count + " times.";
			}
		} else {
			sprofile.cacheString(message);
		}
		
		cache.cacheProfile(user.getId(), sprofile);
		
		return null;
	}
	
	public static class SpammingCache {
		
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
	
	public static class SpammingProfile {
		
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
				if(calculateStringSimilarity(str.toLowerCase(), key.toLowerCase()) >= SIMILARITY_THRESHOLD) {
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
	
	public static double calculateStringSimilarity(String s1, String s2) {
		if(s1.length() == s2.length()) {
			//Hamming Distance algorithm
			int different = 0;
			for(int i = 0; i < s1.length(); i++) {
				if(s1.charAt(i) != s2.charAt(i)) different++;
			}
			return (double)(s1.length() - different) / (double)s1.length();
		} else {
			//Levenshtein Distance algorithm, credits to net.ricecode
			LevenshteinDistanceStrategy strategy = new LevenshteinDistanceStrategy();
			return strategy.score(s1, s2);
		}
	}

}
