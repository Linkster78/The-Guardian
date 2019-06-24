package com.tek.guardian.commands;

import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public abstract class Command {
	
	private String name;
	private List<String> aliases;
	private String syntax;
	private String description;
	private boolean displayed;
	
	public Command(String name, List<String> aliases, String syntax, String description, boolean displayed) {
		this.name = name;
		this.aliases = aliases;
		this.syntax = syntax;
		this.description = description;
		this.displayed = displayed;
	}
	
	public abstract boolean call(JDA jda, Member member, Guild guild, TextChannel channel, String label, String[] args);
	
	public String getFormattedSyntax() {
		return getName() + (getSyntax() == null ? "" : " " + getSyntax());
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getAliases() {
		return aliases;
	}
	
	public String getSyntax() {
		return syntax;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isDisplayed() {
		return displayed;
	}
	
}
