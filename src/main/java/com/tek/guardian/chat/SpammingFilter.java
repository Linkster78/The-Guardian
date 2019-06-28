package com.tek.guardian.chat;

import java.util.Optional;

import com.tek.guardian.cache.SpammingCache;
import com.tek.guardian.cache.SpammingProfile;
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
