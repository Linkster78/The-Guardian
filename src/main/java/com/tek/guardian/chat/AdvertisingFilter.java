package com.tek.guardian.chat;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.entities.User;

public class AdvertisingFilter implements ChatFilter {

	@Override
	public String filterChat(User user, String message, ServerProfile profile) {
		if(!profile.isModerateAdvertising()) return null;
		String lowerMessage = message.toLowerCase();
		if(Reference.URL_REGEX.matcher(message).find() 
				&& (lowerMessage.contains("check") || lowerMessage.contains("look")
				|| lowerMessage.contains("advertis") || lowerMessage.contains("sub"))) return "The message contained unauthorized advertising.";
		if((lowerMessage.contains("invite") && lowerMessage.contains("code"))
				|| lowerMessage.contains("discord.gg")) return "The message contained a discord invite link.";
		return null;
	}

}
