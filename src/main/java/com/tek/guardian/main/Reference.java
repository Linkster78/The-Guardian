package com.tek.guardian.main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;

public class Reference {
	
	public static final String BOOT = "👢";
	public static final String HAMMER = "🔨";
	public static final String SILENCE = "🔇";
	public static final String DEAF = "🎧";
	public static final String DANGER = "☠️";
	public static final String WARNING = "⚠️";
	public static final String GOOD = "✓";
	
	public static final Pattern TAG_REGEX = Pattern.compile("^(\\w|\\s)+#\\d{4}$");
	public static final Pattern SNOWFLAKE_REGEX = Pattern.compile("^\\d+$");
	
	public static final String CONFIG_PATH = "./config.json";
	public static final String DATABASE = "guardian";
	
	public static EmbedBuilder formatEmbed(JDA jda, String title) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		SelfUser self = jda.getSelfUser();
		
		return new EmbedBuilder()
				.setAuthor(title, null, self.getAvatarUrl())
				.setFooter("Executed at " + timeFormatter.format(now) + " EST");
	}
	
	public static Optional<Member> memberFromString(Guild guild, String str) {
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
	
	public static Optional<TextChannel> textChannelFromString(Guild guild, String str) {
		TextChannel channel;
		
		if(Reference.SNOWFLAKE_REGEX.matcher(str).matches()) {
			channel = guild.getTextChannelById(str);
			if(channel != null) return Optional.of(channel);
		}
		
		Iterator<TextChannel> channelIterator = guild.getTextChannels().iterator();
		while(channelIterator.hasNext()) {
			channel = channelIterator.next();
			if(channel.getName().equalsIgnoreCase(str) || channel.getAsMention().equalsIgnoreCase(str)) return Optional.of(channel);
		}
		
		return Optional.empty();
	}
	
	public static Optional<Category> categoryFromString(Guild guild, String str) {
		Category category;
		
		if(Reference.SNOWFLAKE_REGEX.matcher(str).matches()) {
			category = guild.getCategoryById(str);
			if(category != null) return Optional.of(category);
		}
		
		Iterator<Category> categoryIterator = guild.getCategories().iterator();
		while(categoryIterator.hasNext()) {
			category = categoryIterator.next();
			if(category.getName().equalsIgnoreCase(str)) return Optional.of(category);
		}
		
		return Optional.empty();
	}
	
	public static Optional<Role> roleFromString(Guild guild, String str) {
		Role role;
		
		if(Reference.SNOWFLAKE_REGEX.matcher(str).matches()) {
			role = guild.getRoleById(str);
			if(role != null) return Optional.of(role);
		}
		
		Iterator<Role> roleIterator = guild.getRoles().iterator();
		while(roleIterator.hasNext()) {
			role = roleIterator.next();
			if(role.getName().equalsIgnoreCase(str) || role.getAsMention().equalsIgnoreCase(str)) return Optional.of(role);
		}
		
		return Optional.empty();
	}
	
	public static int timeToMillis(String str) throws IllegalArgumentException {
		if(str.length() <= 1) throw new IllegalArgumentException("Invalid time string.");
		char timeCharacter = Character.toLowerCase(str.charAt(str.length() - 1));
		String timeString = str.substring(0, str.length() - 1);
		if(!isInteger(timeString)) throw new IllegalArgumentException("Invalid time string.");
		if(timeCharacter == 's') {
			return (int) TimeUnit.SECONDS.toMillis(Integer.parseInt(timeString));
		} else if(timeCharacter == 'm') {
			return (int) TimeUnit.MINUTES.toMillis(Integer.parseInt(timeString));
		} else if(timeCharacter == 'h') {
			return (int) TimeUnit.HOURS.toMillis(Integer.parseInt(timeString));
		} else if(timeCharacter == 'd') {
			return (int) TimeUnit.DAYS.toMillis(Integer.parseInt(timeString));
		} else {
			throw new IllegalArgumentException("Invalid time string.");
		}
	}
	
	public static String formatTime(long millis) {
		if(millis < TimeUnit.MINUTES.toMillis(1)) return TimeUnit.MILLISECONDS.toSeconds(millis) + " second(s)";
		if(millis < TimeUnit.HOURS.toMillis(1)) return TimeUnit.MILLISECONDS.toMinutes(millis) + " minute(s)";
		if(millis < TimeUnit.DAYS.toMillis(1)) return TimeUnit.MILLISECONDS.toHours(millis) + " hour(s)";
		return TimeUnit.MILLISECONDS.toDays(millis) + " day(s)";
	}
	
	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	
}
