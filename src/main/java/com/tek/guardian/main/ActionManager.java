package com.tek.guardian.main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.data.TemporaryAction;
import com.tek.guardian.enums.Action;
import com.tek.guardian.enums.BotRole;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction.PaginationIterator;

public class ActionManager {
	
	public void mute(Member author, Member member, ServerProfile profile, TextChannel channel, String reason) {
		Guild guild = author.getGuild();
		Role r = guild.getRoleById(profile.getRoleMap().get(BotRole.MUTED.name()));
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage("You have been muted in the server **" + guild.getName() + "** for the reason: `" + reason + "`").queue(m -> {
					guild.addRoleToMember(member, r).queue();
				}, e -> {
					guild.addRoleToMember(member, r).queue();
				});
			}, e -> {
				guild.addRoleToMember(member, r).queue();
			});
			
			channel.sendMessage("Successfully muted " + member.getUser().getAsMention() + ". `" + reason + "`").queue();
			
			MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "User Muted")
					.setColor(Color.black)
					.setDescription("A user was muted.")
					.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
					.addField("Staff Member ID", author.getId(), true)
					.addField("Muted User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
					.addField("Muted User ID", member.getId(), true)
					.addField("Mute Duration", "Indefinite", true)
					.build();
			
			log(embed, guild, profile);
		} else {
			channel.sendMessage("Couldn't mute " + member.getUser().getAsMention()).queue();
		}
	}
	
	public void temporarilyMute(Member author, Member member, ServerProfile profile, long time, TextChannel channel, String reason) {
		Guild guild = author.getGuild();
		Role r = guild.getRoleById(profile.getRoleMap().get(BotRole.MUTED.name()));
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage("You have been temporarily muted in the server **" + guild.getName() + "** for **" + Reference.formatTime(time) + "** for the reason: `" + reason + "`").queue(m -> {
					guild.addRoleToMember(member, r).queue();
				}, e -> {
					guild.addRoleToMember(member, r).queue();
				});
			}, e -> {
				guild.addRoleToMember(member, r).queue();
			});
			
			TemporaryAction action = new TemporaryAction(member.getId(), guild.getId(), Action.TEMPMUTE, System.currentTimeMillis(), time);
			Guardian.getInstance().getMongoAdapter().createTemporaryAction(action);
			
			channel.sendMessage("Temporarily muted " + member.getUser().getAsMention() + " for " + Reference.formatTime(time) + ". `" + reason + "`").queue();
		
			MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "User Muted")
					.setColor(Color.black)
					.setDescription("A user was muted.")
					.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
					.addField("Staff Member ID", author.getId(), true)
					.addField("Muted User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
					.addField("Muted User ID", member.getId(), true)
					.addField("Mute Duration", Reference.formatTime(time), true)
					.build();
			
			log(embed, guild, profile);
		} else {
			channel.sendMessage("Couldn't mute " + member.getUser().getAsMention()).queue();
		}
	}
	
	public void unmute(Member author, Member member, ServerProfile profile, TextChannel channel) {
		Guild guild = author.getGuild();
		Role r = guild.getRoleById(profile.getRoleMap().get(BotRole.MUTED.name()));
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage("You have been unmuted in the server **" + guild.getName() + "**.").queue(m -> {
					guild.removeRoleFromMember(member, r).queue();
				}, e -> {
					guild.removeRoleFromMember(member, r).queue();
				});
			}, e -> {
				guild.removeRoleFromMember(member, r).queue();
			});
			
			for(TemporaryAction action : Guardian.getInstance().getMongoAdapter().getTemporaryActions()) {
				if(action.getUserId().equals(member.getId())) {
					if(action.getAction().equals(Action.TEMPMUTE)) {
						Guardian.getInstance().getMongoAdapter().removeTemporaryAction(action);
					}
				}
			}
			
			if(channel != null) channel.sendMessage("Successfully unmuted " + member.getUser().getAsMention() + ".").queue();
		
			MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "User Unmuted")
					.setColor(Color.white)
					.setDescription("A user was unmuted.")
					.addField("Staff Member", author == null ? "Temporary Action" : author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
					.addField("Staff Member ID", author == null ? "Temporary Action" : author.getId(), true)
					.addField("Unmuted User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
					.addField("Unmuted User ID", member.getId(), true)
					.build();
			
			log(embed, guild, profile);
		} else {
			channel.sendMessage("Couldn't unmute " + member.getUser().getAsMention()).queue();
		}
	}
	
	public void deafen(Member author, Member member, TextChannel channel, String reason, ServerProfile profile) {
		Guild guild = author.getGuild();
		
		if(guild.getSelfMember().canInteract(member)) {
			if(member.getVoiceState().inVoiceChannel()) {
				member.getUser().openPrivateChannel().queue(pm -> {
					pm.sendMessage("You have been deafened in the server **" + guild.getName() + "** for the reason: `" + reason + "`").queue(m -> {
						member.deafen(true).queue();
					}, e -> {
						member.deafen(true).queue();
					});
				}, e -> {
					member.deafen(true).queue();
				});
				
				channel.sendMessage("Successfully deafened " + member.getUser().getAsMention() + ". `" + reason + "`").queue();
			
				MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "User Deafened")
						.setColor(Color.black)
						.setDescription("A user was deafened.")
						.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
						.addField("Staff Member ID", author.getId(), true)
						.addField("Deafened User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
						.addField("Deafened User ID", member.getId(), true)
						.addField("Deafen Duration", "Indefinite", true)
						.build();
				
				log(embed, guild, profile);
			} else {
				channel.sendMessage("The user " + member.getUser().getAsMention() + " is not in a voice channel.").queue();
			}
		} else {
			channel.sendMessage("Couldn't deafen " + member.getUser().getAsMention()).queue();
		}
	}
	
	public void temporarilyDeafen(Member author, Member member, long time, TextChannel channel, String reason, ServerProfile profile) {
		Guild guild = author.getGuild();
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage("You have been temporarily deafened in the server **" + guild.getName() + "** for **" + Reference.formatTime(time) + "** for the reason: `" + reason + "`").queue(m -> {
					member.deafen(true).queue();
				}, e -> {
					member.deafen(true).queue();
				});
			}, e -> {
				member.deafen(true).queue();
			});
			
			TemporaryAction action = new TemporaryAction(member.getId(), guild.getId(), Action.TEMPDEAFEN, System.currentTimeMillis(), time);
			Guardian.getInstance().getMongoAdapter().createTemporaryAction(action);
			
			channel.sendMessage("Temporarily deafened " + member.getUser().getAsMention() + " for " + Reference.formatTime(time) + ". `" + reason + "`").queue();
		
			MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "User Deafened")
					.setColor(Color.black)
					.setDescription("A user was deafened.")
					.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
					.addField("Staff Member ID", author.getId(), true)
					.addField("Deafened User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
					.addField("Deafened User ID", member.getId(), true)
					.addField("Deafen Duration", Reference.formatTime(time), true)
					.build();
			
			log(embed, guild, profile);
		} else {
			channel.sendMessage("Couldn't deafen " + member.getUser().getAsMention()).queue();
		}
	}
	
	public void undeafen(Member author, Member member, TextChannel channel, ServerProfile profile) {
		Guild guild = author.getGuild();
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage("You have been undeafened in the server **" + guild.getName() + "**").queue(m -> {
					member.deafen(false).queue();
				}, e -> {
					member.deafen(false).queue();
				});
			}, e -> {
				member.deafen(false).queue();
			});
			
			for(TemporaryAction action : Guardian.getInstance().getMongoAdapter().getTemporaryActions()) {
				if(action.getUserId().equals(member.getId())) {
					if(action.getAction().equals(Action.TEMPDEAFEN)) {
						Guardian.getInstance().getMongoAdapter().removeTemporaryAction(action);
					}
				}
			}
			
			if(channel != null) channel.sendMessage("Successfully undeafened " + member.getUser().getAsMention() + ".").queue();
		
			MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "User Undeafened")
					.setColor(Color.white)
					.setDescription("A user was undeafened.")
					.addField("Staff Member", author == null ? "Temporary Action" : author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
					.addField("Staff Member ID", author == null ? "Temporary Action" : author.getId(), true)
					.addField("Undeafened User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
					.addField("Undeafened User ID", member.getId(), true)
					.build();
			
			log(embed, guild, profile);
		} else {
			channel.sendMessage("Couldn't undeafen " + member.getUser().getAsMention()).queue();
		}
	}
	
	public void ban(Member author, Member member, TextChannel channel, String reason, ServerProfile profile) {
		Guild guild = author.getGuild();
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage("You were banned from the server **" + guild.getName() + "** for the reason: `" + reason + "`").queue(m -> {
					guild.ban(member, 0, reason).queue();
				}, e -> {
					guild.ban(member, 0, reason).queue();
				});
			}, e -> {
				guild.ban(member, 0, reason).queue();
			});
			
			channel.sendMessage("Successfully banned " + member.getUser().getAsMention() + " from the server. `" + reason + "`").queue();
		
			MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "User Banned")
					.setColor(Color.red)
					.setDescription("A user was banned.")
					.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
					.addField("Staff Member ID", author.getId(), true)
					.addField("Banned User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
					.addField("Banned User ID", member.getId(), true)
					.addField("Ban Duration", "Indefinite", true)
					.build();
			
			log(embed, guild, profile);
		} else {
			channel.sendMessage("Couldn't ban " + member.getUser().getAsMention()).queue();
		}
	}
	
	public void temporarilyBan(Member author, Member member, long time, TextChannel channel, String reason, ServerProfile profile) {
		Guild guild = author.getGuild();
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage("You have been temporarily banned in the server **" + guild.getName() + "** for **" + Reference.formatTime(time) + "** for the reason: `" + reason + "`").queue(m -> {
					guild.ban(member, 0, reason).queue();
				}, e -> {
					guild.ban(member, 0, reason).queue();
				});
			}, e -> {
				guild.ban(member, 0, reason).queue();
			});
				
			TemporaryAction action = new TemporaryAction(member.getId(), guild.getId(), Action.TEMPBAN, System.currentTimeMillis(), time);
			Guardian.getInstance().getMongoAdapter().createTemporaryAction(action);
				
			channel.sendMessage("Temporarily banned " + member.getUser().getAsMention() + " for " + Reference.formatTime(time) + ". `" + reason + "`").queue();
		
			MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "User Banned")
					.setColor(Color.red)
					.setDescription("A user was banned.")
					.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
					.addField("Staff Member ID", author.getId(), true)
					.addField("Banned User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
					.addField("Banned User ID", member.getId(), true)
					.addField("Ban Duration", Reference.formatTime(time), true)
					.build();
			
			log(embed, guild, profile);
		} else {
			channel.sendMessage("Couldn't ban " + member.getUser().getAsMention()).queue();
		}
	}
	
	public void unban(Guild guild, String userId, ServerProfile profile) {
		guild.retrieveBanList().queue(bans -> {
			bans.stream().filter(ban -> ban.getUser().getId().equals(userId)).forEach(ban -> guild.unban(ban.getUser()).queue());
		});
	}
	
	public void kick(Member author, Member member, TextChannel channel, String reason, ServerProfile profile) {
		Guild guild = author.getGuild();
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage("You were kicked from the server **" + guild.getName() + "** for the reason: `" + reason + "`").queue(m -> {
					guild.kick(member, reason).queue();
				}, e -> {
					guild.kick(member, reason).queue();
				});
			}, e -> {
				guild.kick(member, reason).queue();
			});
			
			channel.sendMessage("Successfully kicked " + member.getUser().getAsMention() + " from the server. `" + reason + "`").queue();
		
			MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "User Kicked")
					.setColor(Color.orange)
					.setDescription("A user was kicked.")
					.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
					.addField("Staff Member ID", author.getId(), true)
					.addField("Kicked User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
					.addField("Kicked User ID", member.getId(), true)
					.build();
			
			log(embed, guild, profile);
		} else {
			channel.sendMessage("Couldn't kick " + member.getUser().getAsMention()).queue();
		}
	}
	
	public void clear(Member author, TextChannel channel, int amount, ServerProfile profile) {
		int i = 0;
		List<Message> toDelete = new ArrayList<Message>(amount);
		PaginationIterator<Message> messageIterator = channel.getIterableHistory().iterator();
		Message message;
		while(messageIterator.hasNext()) {
			if(i >= amount) break;
			message = messageIterator.next();
			if(!message.getId().equals(channel.getLatestMessageId())) {
				toDelete.add(message);
			} else {
				continue;
			}
			i++;
		}
		channel.deleteMessages(toDelete).queue(v -> {
			channel.sendMessage("Cleared **" + toDelete.size() + "** messages.").queue();
		});
		
		MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "Messages Cleared")
				.setColor(Color.gray)
				.setDescription("A message clearing/purge took place.")
				.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
				.addField("Staff Member ID", author.getId(), true)
				.addField("Message Count", toDelete.size() + " messages.", true)
				.build();
		
		log(embed, channel.getGuild(), profile);
	}
	
	public void clearUser(Member author, Member member, TextChannel channel, int amount, ServerProfile profile) {
		int i = 0;
		List<Message> toDelete = new ArrayList<Message>(amount);
		PaginationIterator<Message> messageIterator = channel.getIterableHistory().iterator();
		Message message;
		while(messageIterator.hasNext()) {
			if(i >= amount) break;
			message = messageIterator.next();
			if(!message.getId().equals(channel.getLatestMessageId())) {
				if(message.getAuthor().getId().equals(member.getId())) {
					toDelete.add(message);
					i++;
				}
			}
		}
		channel.deleteMessages(toDelete).queue(v -> {
			channel.sendMessage("Cleared **" + toDelete.size() + "** messages from " + member.getAsMention() + ".").queue();
		});
		
		MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "Messages Cleared")
				.setColor(Color.gray)
				.setDescription("A message clearing/purge took place.")
				.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
				.addField("Staff Member ID", author.getId(), true)
				.addField("Message Count", toDelete.size() + " messages.", true)
				.addField("Cleared User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
				.build();
		
		log(embed, channel.getGuild(), profile);
	}
	
	public void lock(Member author, ServerProfile profile, TextChannel in, TextChannel channel) {
		profile.getLockedChannels().add(channel.getId());
		profile.save();
		in.sendMessage("Locked the channel " + channel.getAsMention() + ".").queue();
		
		MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "Channel Locked")
				.setColor(Color.yellow)
				.setDescription("A channel was locked.")
				.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
				.addField("Staff Member ID", author.getId(), true)
				.addField("Locked Channel", "#" + channel.getName(), true)
				.addField("Locked Channel ID", channel.getId(), true)
				.build();
		
		log(embed, channel.getGuild(), profile);
	}
	
	public void unlock(Member author, ServerProfile profile, TextChannel in, TextChannel channel) {
		profile.getLockedChannels().remove(channel.getId());
		profile.save();
		in.sendMessage("Unlocked the channel " + channel.getAsMention() + ".").queue();
		
		MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "Channel Unlocked")
				.setColor(Color.yellow)
				.setDescription("A channel was unlocked.")
				.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
				.addField("Staff Member ID", author.getId(), true)
				.addField("Unlocked Channel", "#" + channel.getName(), true)
				.addField("Unlocked Channel ID", channel.getId(), true)
				.build();
		
		log(embed, channel.getGuild(), profile);
	}
	
	public void log(MessageEmbed embed, Guild guild, ServerProfile profile) {
		if(profile.getLogChannel() != null) {
			TextChannel logChannel = guild.getTextChannelById(profile.getLogChannel());
			if(logChannel != null) {
				logChannel.sendMessage(embed).queue();
			}
		}
	}
	
}
