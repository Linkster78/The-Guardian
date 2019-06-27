package com.tek.guardian.data;

import java.util.ArrayList;
import java.util.List;

import com.tek.guardian.main.Guardian;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity("user_profiles")
public class UserProfile {
	
	@Id
	private String id;
	private String guildId;
	private String userId;
	private List<Warning> warnings;
	
	public UserProfile(String guildId, String userId) {
		this.id = guildId + userId;
		this.guildId = guildId;
		this.userId = userId;
		this.warnings = new ArrayList<Warning>();
	}

	public void save() {
		Guardian.getInstance().getMongoAdapter().saveUserProfile(this);
	}
	
	public void pushWarning(Warning warning) {
		warnings.add(warning);
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
	
	public List<Warning> getWarnings() {
		return warnings;
	}

	public static class Warning {
		
		private String authorId;
		private String warning;
		
		public Warning(String authorId, String warning) {
			this.authorId = authorId;
			this.warning = warning;
		}
		
		public String getAuthorId() {
			return authorId;
		}
		
		public String getWarning() {
			return warning;
		}
		
	}
	
}
