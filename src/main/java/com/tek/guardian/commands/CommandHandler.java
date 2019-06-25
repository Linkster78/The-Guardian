package com.tek.guardian.commands;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandHandler extends ListenerAdapter {
	
	private List<Command> commands;
	
	public CommandHandler() {
		commands = new ArrayList<Command>();
	}
	
	public void registerCommand(Command command) {
		commands.add(command);
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Member member = event.getMember();
		Guild guild = event.getGuild();
		TextChannel channel = event.getChannel();
		
		if(member.getUser().isBot()) return;
		
		String label = event.getMessage().getContentRaw();
		String[] tokens = label.split(" ");
		String[] args = new String[tokens.length - 1];
		for(int i = 1; i < tokens.length; i++) args[i - 1] = tokens[i];
		
		ServerProfile profile = Guardian.getInstance().getMongoAdapter().getServerProfile(guild);
		if(label.startsWith(profile.getPrefix())) {
			if(profile.canSendCommand(channel.getId())) {
				if(tokens[0].length() != profile.getPrefix().length()) {
					String command = tokens[0].substring(profile.getPrefix().length());
					for(Command cmd : commands) {
						if(cmd.getName().equalsIgnoreCase(command)) {
							if(!cmd.call(event.getJDA(), profile, member, guild, channel, label, args)) {
								channel.sendMessage("**Invalid syntax.** `" + profile.getPrefix() + cmd.getFormattedSyntax() + "`").queue();
							}
							
							if(profile.doesDeleteCommands()) {
								event.getMessage().delete().queue();
							}
							
							return;
						}
					}
					
					if(profile.doesDeleteCommands()) {
						event.getMessage().delete().queue();
					}
					
					if(profile.doesReplyUnknown()) {
						channel.sendMessage("**The command** `" + command + "` **does not exist.**").queue();
					}
				} else {
					if(profile.doesDeleteCommands()) {
						event.getMessage().delete().queue();
					}
				}
			} else {
				if(profile.doesDeleteCommands()) {
					event.getMessage().delete().queue();
				}
			}
		}
	}
	
	public static EmbedBuilder formatEmbed(JDA jda, String title) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		SelfUser self = jda.getSelfUser();
		
		return new EmbedBuilder()
				.setAuthor(title, null, self.getAvatarUrl())
				.setFooter("Executed at " + timeFormatter.format(now) + " EST");
	}
	
	public static Optional<Member> fromString(Guild guild, String str) {
		List<Member> members;
		Member member;
		
		if(Reference.SNOWFLAKE_REGEX.matcher(str).matches()) {
			member = guild.getMemberById(str);
			if(member != null) return Optional.of(member);
		}
		
		if(Reference.TAG_REGEX.matcher(str).matches()) {
			member = guild.getMemberByTag(str);
			if(member != null) return Optional.of(member);
		}
		
		members = guild.getMembersByName(str, true);
		if(!members.isEmpty()) return Optional.of(members.get(0));
		
		members = guild.getMembersByNickname(str, true);
		if(!members.isEmpty()) return Optional.of(members.get(0));
		
		Iterator<Member> memberIterator = guild.getMemberCache().iterator();
		while(memberIterator.hasNext()) {
			member = memberIterator.next();
			if(member.getAsMention().equals(str) || member.getUser().getAsMention().equals(str)) return Optional.of(member);
		}
		
		return Optional.empty();
	}
	
	public List<Command> getCommands() {
		return commands;
	}
	
}
