package com.tek.guardian.commands;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.tek.guardian.data.ReactionRole;
import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction.PaginationIterator;

public class ReactionRoleCommand extends Command {

	private EventWaiter waiter;
	
	public ReactionRoleCommand(EventWaiter waiter) {
		super("reactionrole", Arrays.asList("rrole"), "<channel> <message id>", "Adds a reaction role to a message.", true);
		this.waiter = waiter;
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length == 2) {
			if(member.hasPermission(Permission.MANAGE_ROLES)) {
				Optional<TextChannel> channelOpt = Reference.textChannelFromString(guild, args[0]);
				if(channelOpt.isPresent()) {
					Optional<Message> messageOpt = findMessage(channelOpt.get(), args[1]);
					if(messageOpt.isPresent()) {
						channel.sendMessage(Reference.embedInfo(jda, "Reaction Role Creation: What's the name of the reaction role?")).queue(message -> {
							PollCommand.waitForMessage(waiter, channel, member, response -> {
								Optional<Role> roleOpt = Reference.roleFromString(guild, response);
								if(roleOpt.isPresent()) {
									message.editMessage(Reference.embedInfo(jda, "Reaction Role Creation: Okay, `" + roleOpt.get().getName() + "` selected. React to this message with the emote you want.")).queue(message1 -> {
										waitForReaction(waiter, channel, member, reaction -> {
											if(reaction.getReactionEmote().isEmote()) {
												ReactionRole reactionRole = new ReactionRole(guild.getId(), messageOpt.get().getId(), reaction.getReactionEmote().getId(), roleOpt.get().getId(), false);
												if(Guardian.getInstance().getMongoAdapter().getReactionRoles(reactionRole.getMessageId(), reactionRole.getEmoteId()).isEmpty()) {
													Guardian.getInstance().getMongoAdapter().saveReactionRole(reactionRole);
													messageOpt.get().addReaction(reaction.getReactionEmote().getEmote()).queue(r -> {
														message1.editMessage(Reference.embedSuccess(jda, "Success! The reaction role has been attached to your message.")).queue();
													});
												} else {
													message.editMessage(Reference.embedError(jda, "Failure! That message already has a reaction role set to the emote.")).queue();
												}
											} else {
												ReactionRole reactionRole = new ReactionRole(guild.getId(), messageOpt.get().getId(), reaction.getReactionEmote().getEmoji(), roleOpt.get().getId(), true);
												if(Guardian.getInstance().getMongoAdapter().getReactionRoles(reactionRole.getMessageId(), reactionRole.getEmoteId()).isEmpty()) {
													Guardian.getInstance().getMongoAdapter().saveReactionRole(reactionRole);
													messageOpt.get().addReaction(reaction.getReactionEmote().getEmoji()).queue(r -> {
														message1.editMessage(Reference.embedSuccess(jda, "Success! The reaction role has been attached to your message.")).queue();
													});
												} else {
													message.editMessage(Reference.embedError(jda, "Failure! That message already has a reaction role set to the emote.")).queue();
												}
											}
										}, () -> {
											channel.sendMessage(Reference.embedError(jda, "The reaction role creation timed out.")).queue();
										});
									});
								} else {
									channel.sendMessage(Reference.embedError(jda, "Invalid role. Reaction role creation cancelled.")).queue();
								}
							}, () -> {
								channel.sendMessage(Reference.embedError(jda, "The reaction role creation timed out.")).queue();
							});
						});
					} else {
						channel.sendMessage(Reference.embedError(jda, "Sorry. The message you specified either doesn't exist or is too old.")).queue();
					}
				} else {
					channel.sendMessage(Reference.embedError(jda, "The channel specified does not exist.")).queue();
				}
			} else {
				channel.sendMessage(Reference.embedError(jda, "You cannot create reaction roles.")).queue();
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public static void waitForReaction(EventWaiter waiter, TextChannel channel, Member member, Consumer<MessageReaction> reactionCallback, Runnable timeout) {
		waiter.waitForEvent(GuildMessageReactionAddEvent.class, (GuildMessageReactionAddEvent event) -> {
			return event.getChannel().getId().equals(channel.getId()) && member.getId().equals(event.getMember().getId());
		}, (GuildMessageReactionAddEvent event) -> {
			reactionCallback.accept(event.getReaction());
		}, 1, TimeUnit.MINUTES, () -> timeout.run());
	}
	
	public Optional<Message> findMessage(TextChannel channel, String id) {
		int count = 100;
		PaginationIterator<Message> messageIterator = channel.getIterableHistory().iterator();
		while(messageIterator.hasNext()) {
			if(count <= 0) break;
			Message message = messageIterator.next();
			if(message.getId().equals(id)) return Optional.of(message);
			count--;
		}
		return Optional.empty();
	}

}
