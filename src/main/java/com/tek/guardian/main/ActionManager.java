package com.tek.guardian.main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.data.TemporaryAction;
import com.tek.guardian.data.UserProfile;
import com.tek.guardian.data.UserProfile.Warning;
import com.tek.guardian.enums.Action;
import com.tek.guardian.enums.BotRole;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction.PaginationIterator;

public class ActionManager {
	
	public void mute(Member author, Member member, ServerProfile profile, TextChannel channel, String reason) {
		Guild guild = author.getGuild();
		Role r = guild.getRoleById(profile.getRoleMap().get(BotRole.MUTED.name()));
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage(Reference.embedInfo(author.getJDA(), "You have been muted in the server **" + guild.getName() + "** for the reason: `" + reason + "`")).queue(m -> {
					guild.addRoleToMember(member, r).queue();
				}, e -> {
					guild.addRoleToMember(member, r).queue();
				});
			}, e -> {
				guild.addRoleToMember(member, r).queue();
			});
			
			channel.sendMessage(Reference.embedSuccess(author.getJDA(), "Successfully muted " + member.getUser().getAsMention() + ". `" + reason + "`")).queue();
			
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
			channel.sendMessage(Reference.embedError(author.getJDA(), "Couldn't mute " + member.getUser().getAsMention() + ".")).queue();
		}
	}
	
	public void temporarilyMute(Member author, Member member, ServerProfile profile, long time, TextChannel channel, String reason) {
		Guild guild = author.getGuild();
		Role r = guild.getRoleById(profile.getRoleMap().get(BotRole.MUTED.name()));
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage(Reference.embedInfo(author.getJDA(), "You have been temporarily muted in the server **" + guild.getName() + "** for **" + Reference.formatTime(time) + "** for the reason: `" + reason + "`")).queue(m -> {
					guild.addRoleToMember(member, r).queue();
				}, e -> {
					guild.addRoleToMember(member, r).queue();
				});
			}, e -> {
				guild.addRoleToMember(member, r).queue();
			});
			
			TemporaryAction action = new TemporaryAction(member.getId(), guild.getId(), Action.TEMPMUTE, System.currentTimeMillis(), time);
			Guardian.getInstance().getMongoAdapter().createTemporaryAction(action);
			
			channel.sendMessage(Reference.embedSuccess(author.getJDA(), "Temporarily muted " + member.getUser().getAsMention() + " for " + Reference.formatTime(time) + ". `" + reason + "`")).queue();
		
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
			channel.sendMessage(Reference.embedError(author.getJDA(), "Couldn't mute " + member.getUser().getAsMention() + ".")).queue();
		}
	}
	
	public void unmute(Member author, Member member, ServerProfile profile, TextChannel channel) {
		Guild guild = member.getGuild();
		Role r = guild.getRoleById(profile.getRoleMap().get(BotRole.MUTED.name()));
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage(Reference.embedInfo(member.getJDA(), "You have been unmuted in the server **" + guild.getName() + "**.")).queue(m -> {
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
			
			if(channel != null) channel.sendMessage(Reference.embedSuccess(member.getJDA(), "Successfully unmuted " + member.getUser().getAsMention() + ".")).queue();
		
			MessageEmbed embed = Reference.formatEmbed(member.getJDA(), "User Unmuted")
					.setColor(Color.white)
					.setDescription("A user was unmuted.")
					.addField("Staff Member", author == null ? "Temporary Action" : author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
					.addField("Staff Member ID", author == null ? "Temporary Action" : author.getId(), true)
					.addField("Unmuted User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
					.addField("Unmuted User ID", member.getId(), true)
					.build();
			
			log(embed, guild, profile);
		} else {
			channel.sendMessage(Reference.embedError(author.getJDA(), "Couldn't unmute " + member.getUser().getAsMention() + ".")).queue();
		}
	}
	
	public void deafen(Member author, Member member, TextChannel channel, String reason, ServerProfile profile) {
		Guild guild = author.getGuild();
		
		if(guild.getSelfMember().canInteract(member)) {
			if(member.getVoiceState().inVoiceChannel()) {
				member.getUser().openPrivateChannel().queue(pm -> {
					pm.sendMessage(Reference.embedInfo(author.getJDA(), "You have been deafened in the server **" + guild.getName() + "** for the reason: `" + reason + "`")).queue(m -> {
						member.deafen(true).queue();
					}, e -> {
						member.deafen(true).queue();
					});
				}, e -> {
					member.deafen(true).queue();
				});
				
				channel.sendMessage(Reference.embedSuccess(author.getJDA(), "Successfully deafened " + member.getUser().getAsMention() + ". `" + reason + "`")).queue();
			
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
				channel.sendMessage(Reference.embedError(author.getJDA(), "The user " + member.getUser().getAsMention() + " is not in a voice channel.")).queue();
			}
		} else {
			channel.sendMessage(Reference.embedError(author.getJDA(), "Couldn't deafen " + member.getUser().getAsMention() + ".")).queue();
		}
	}
	
	public void temporarilyDeafen(Member author, Member member, long time, TextChannel channel, String reason, ServerProfile profile) {
		Guild guild = author.getGuild();
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage(Reference.embedInfo(author.getJDA(), "You have been temporarily deafened in the server **" + guild.getName() + "** for **" + Reference.formatTime(time) + "** for the reason: `" + reason + "`")).queue(m -> {
					member.deafen(true).queue();
				}, e -> {
					member.deafen(true).queue();
				});
			}, e -> {
				member.deafen(true).queue();
			});
			
			TemporaryAction action = new TemporaryAction(member.getId(), guild.getId(), Action.TEMPDEAFEN, System.currentTimeMillis(), time);
			Guardian.getInstance().getMongoAdapter().createTemporaryAction(action);
			
			channel.sendMessage(Reference.embedSuccess(author.getJDA(), "Temporarily deafened " + member.getUser().getAsMention() + " for " + Reference.formatTime(time) + ". `" + reason + "`")).queue();
		
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
			channel.sendMessage(Reference.embedError(author.getJDA(), "Couldn't deafen " + member.getUser().getAsMention() + ".")).queue();
		}
	}
	
	public void undeafen(Member author, Member member, TextChannel channel, ServerProfile profile) {
		Guild guild = member.getGuild();
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage(Reference.embedInfo(member.getJDA(), "You have been undeafened in the server **" + guild.getName() + "**.")).queue(m -> {
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
			
			if(channel != null) channel.sendMessage(Reference.embedSuccess(member.getJDA(), "Successfully undeafened " + member.getUser().getAsMention() + ".")).queue();
		
			MessageEmbed embed = Reference.formatEmbed(member.getJDA(), "User Undeafened")
					.setColor(Color.white)
					.setDescription("A user was undeafened.")
					.addField("Staff Member", author == null ? "Temporary Action" : author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
					.addField("Staff Member ID", author == null ? "Temporary Action" : author.getId(), true)
					.addField("Undeafened User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
					.addField("Undeafened User ID", member.getId(), true)
					.build();
			
			log(embed, guild, profile);
		} else {
			channel.sendMessage(Reference.embedError(member.getJDA(), "Couldn't undeafen " + member.getUser().getAsMention() + ".")).queue();
		}
	}
	
	public void ban(Member author, Member member, TextChannel channel, String reason, ServerProfile profile) {
		Guild guild = author.getGuild();
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage(Reference.embedInfo(author.getJDA(), "You were banned from the server **" + guild.getName() + "** for the reason: `" + reason + "`")).queue(m -> {
					guild.ban(member, 0, reason).queue();
				}, e -> {
					guild.ban(member, 0, reason).queue();
				});
			}, e -> {
				guild.ban(member, 0, reason).queue();
			});
			
			channel.sendMessage(Reference.embedSuccess(author.getJDA(), "Successfully banned " + member.getUser().getAsMention() + " from the server. `" + reason + "`")).queue();
		
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
			channel.sendMessage(Reference.embedError(author.getJDA(), "Couldn't ban " + member.getUser().getAsMention() + ".")).queue();
		}
	}
	
	public void temporarilyBan(Member author, Member member, long time, TextChannel channel, String reason, ServerProfile profile) {
		Guild guild = author.getGuild();
		
		if(guild.getSelfMember().canInteract(member)) {
			member.getUser().openPrivateChannel().queue(pm -> {
				pm.sendMessage(Reference.embedInfo(author.getJDA(), "You have been temporarily banned in the server **" + guild.getName() + "** for **" + Reference.formatTime(time) + "** for the reason: `" + reason + "`")).queue(m -> {
					guild.ban(member, 0, reason).queue();
				}, e -> {
					guild.ban(member, 0, reason).queue();
				});
			}, e -> {
				guild.ban(member, 0, reason).queue();
			});
				
			TemporaryAction action = new TemporaryAction(member.getId(), guild.getId(), Action.TEMPBAN, System.currentTimeMillis(), time);
			Guardian.getInstance().getMongoAdapter().createTemporaryAction(action);
				
			channel.sendMessage(Reference.embedSuccess(author.getJDA(), "Temporarily banned " + member.getUser().getAsMention() + " for " + Reference.formatTime(time) + ". `" + reason + "`")).queue();
		
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
			channel.sendMessage(Reference.embedError(author.getJDA(), "Couldn't ban " + member.getUser().getAsMention() + ".")).queue();
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
				pm.sendMessage(Reference.embedInfo(author.getJDA(), "You were kicked from the server **" + guild.getName() + "** for the reason: `" + reason + "`")).queue(m -> {
					guild.kick(member, reason).queue();
				}, e -> {
					guild.kick(member, reason).queue();
				});
			}, e -> {
				guild.kick(member, reason).queue();
			});
			
			channel.sendMessage(Reference.embedSuccess(author.getJDA(), "Successfully kicked " + member.getUser().getAsMention() + " from the server. `" + reason + "`")).queue();
		
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
			channel.sendMessage(Reference.embedError(author.getJDA(), "Couldn't kick " + member.getUser().getAsMention() + ".")).queue();
		}
	}
	
	public void voiceKick(Member author, Member member, TextChannel in, ServerProfile profile) {
		Guild guild = author.getGuild();
		
		if(guild.getSelfMember().canInteract(member)) {
			VoiceChannel channel = member.getVoiceState().getChannel();
			
			guild.moveVoiceMember(member, null).queue(e -> {
				member.getUser().openPrivateChannel().queue(pm -> pm.sendMessage(Reference.embedInfo(author.getJDA(), "You have been kicked from your voice channel in the server **" + guild.getName() + "**.")).queue(m -> { }, e1 -> { }), e1 -> { });
				
				in.sendMessage(Reference.embedSuccess(author.getJDA(), "Successfully kicked " + member.getUser().getAsMention() + " from his voice channel.")).queue();
			
				MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "User Voice-Kicked")
						.setColor(Color.gray)
						.setDescription("A user was voice-kicked.")
						.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
						.addField("Staff Member ID", author.getId(), true)
						.addField("Voice-Kicked User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
						.addField("Voice-Kicked User ID", member.getId(), true)
						.addField("Voice Channel", channel.getName(), true)
						.addField("Voice Channel ID", channel.getId(), true)
						.build();
				
				log(embed, guild, profile);
			});
		} else {
			in.sendMessage(Reference.embedError(author.getJDA(), "Couldn't voice-kick " + member.getUser().getAsMention() + ".")).queue();
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
				Guardian.getInstance().getMessageCache().decache(message.getId());
			} else {
				continue;
			}
			i++;
		}
		channel.deleteMessages(toDelete).queue(v -> {
			channel.sendMessage(Reference.embedSuccess(author.getJDA(), "Cleared **" + toDelete.size() + "** messages.")).queue();
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
					Guardian.getInstance().getMessageCache().decache(message.getId());
					i++;
				}
			}
		}
		channel.deleteMessages(toDelete).queue(v -> {
			channel.sendMessage(Reference.embedSuccess(author.getJDA(), "Cleared **" + toDelete.size() + "** messages from " + member.getAsMention() + ".")).queue();
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
		in.sendMessage(Reference.embedSuccess(author.getJDA(), "Locked the channel " + channel.getAsMention() + ".")).queue();
		
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
		in.sendMessage(Reference.embedSuccess(author.getJDA(), "Unlocked the channel " + channel.getAsMention() + ".")).queue();
		
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
	
	public void warn(Member author, Member member, TextChannel in, String warningText, ServerProfile profile) {
		UserProfile userProfile = Guardian.getInstance().getMongoAdapter().getUserProfile(member);
		Warning warning = new Warning(author.getId(), warningText);
		userProfile.pushWarning(warning);
		userProfile.save();
		
		in.sendMessage(Reference.embedSuccess(author.getJDA(), "Successfully warned " + member.getAsMention() + " for `" + warningText + "`. He now has **" + userProfile.getWarnings().size() + "** warning(s).")).queue();
	
		member.getUser().openPrivateChannel().queue(pm -> {
			pm.sendMessage(Reference.embedInfo(author.getJDA(), "You have been warned in the server **" + author.getGuild().getName() + "**: `" + warningText + "`.")).queue(m -> {}, e -> {});
		});
		
		MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "Member Warned")
				.setColor(Color.gray)
				.setDescription("A member was warned.")
				.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
				.addField("Staff Member ID", author.getId(), true)
				.addField("Warned User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
				.addField("Warned User ID", member.getId(), true)
				.addField("Warning", warningText, true)
				.addField("Member Warning Count", Integer.toString(userProfile.getWarnings().size()), true)
				.build();
		
		log(embed, author.getGuild(), profile);
	}
	
	public void unwarn(Member author, Member member, TextChannel in, int count, ServerProfile profile, UserProfile userProfile) {
		if(count == Integer.MAX_VALUE) {
			userProfile.getWarnings().clear();
			userProfile.save();
			
			in.sendMessage(Reference.embedSuccess(author.getJDA(), "Cleared the warnings of " + member.getAsMention() + ".")).queue();
			
			MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "Member Warnings Cleared")
					.setColor(Color.gray)
					.setDescription("The warnings of a member were cleared.")
					.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
					.addField("Staff Member ID", author.getId(), true)
					.addField("Cleared User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
					.addField("Cleared User ID", member.getId(), true)
					.build();
			
			log(embed, author.getGuild(), profile);
		} else {
			Warning warning = userProfile.getWarnings().get(count - 1);
			Member warningAuthor = author.getGuild().getMemberById(warning.getAuthorId());
			userProfile.getWarnings().remove(count - 1);
			userProfile.save();
			
			in.sendMessage(Reference.embedSuccess(author.getJDA(), "Warning `" + warning.getWarning() + "` assigned by **" + (warningAuthor == null ? "Unknown User" : warningAuthor.getUser().getName() + "#" + warningAuthor.getUser().getDiscriminator()) + "** removed from " + member.getAsMention() + ".")).queue();
			
			MessageEmbed embed = Reference.formatEmbed(author.getJDA(), "Member Warning Removed")
					.setColor(Color.gray)
					.setDescription("A warning was removed from a member.")
					.addField("Staff Member", author.getUser().getName() + "#" + author.getUser().getDiscriminator(), true)
					.addField("Staff Member ID", author.getId(), true)
					.addField("Unwarned User", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
					.addField("Unwarned User ID", member.getId(), true)
					.addField("Removed Warning", warning.getWarning(), true)
					.addField("Removed Warning Author", warningAuthor == null ? "Unknown User" : warningAuthor.getUser().getName() + "#" + warningAuthor.getUser().getDiscriminator(), true)
					.addField("Removed Warning Author ID", warningAuthor == null ? "Unknown User" : warningAuthor.getId(), true)
					.build();
			
			log(embed, author.getGuild(), profile);
		}
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
