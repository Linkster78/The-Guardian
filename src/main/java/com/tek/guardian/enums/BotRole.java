package com.tek.guardian.enums;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.Permission;

public enum BotRole {
	
	MUTED(Arrays.asList(Permission.VOICE_SPEAK, Permission.MESSAGE_WRITE));
	
	private List<Permission> denies;
	
	private BotRole(List<Permission> denies) {
		this.denies = denies;
	}
	
	public List<Permission> getDenies() {
		return denies;
	}
	
	public String getName() {
		return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
	}
	
}
