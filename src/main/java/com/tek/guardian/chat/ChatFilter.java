package com.tek.guardian.chat;

import com.tek.guardian.data.ServerProfile;

import net.dv8tion.jda.api.entities.User;

public interface ChatFilter {
	
	public String filterChat(User user, String message, ServerProfile profile);
	
}
