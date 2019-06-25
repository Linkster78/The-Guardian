package com.tek.guardian.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction.PaginationIterator;

public class ClearCommand extends Command {

	public ClearCommand() {
		super("clear", Arrays.asList(), "<amount> [user]", "Deletes a bulk of messages.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length >= 1 && args.length <= 2) {
			if(member.hasPermission(Permission.MESSAGE_MANAGE)) {
				if(Reference.isInteger(args[0])) {
					int amount = Integer.parseInt(args[0]);
					
					if(args.length == 1) {
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
					} else if(args.length == 2) {
						Optional<Member> memberOpt = Reference.memberFromString(guild, args[1]);
						
						if(memberOpt.isPresent()) {
							int i = 0;
							List<Message> toDelete = new ArrayList<Message>(amount);
							PaginationIterator<Message> messageIterator = channel.getIterableHistory().iterator();
							Message message;
							while(messageIterator.hasNext()) {
								if(i >= amount) break;
								message = messageIterator.next();
								if(!message.getId().equals(channel.getLatestMessageId())) {
									if(message.getAuthor().getId().equals(memberOpt.get().getId())) {
										toDelete.add(message);
										i++;
									}
								}
							}
							channel.deleteMessages(toDelete).queue(v -> {
								channel.sendMessage("Cleared **" + toDelete.size() + "** messages from " + memberOpt.get().getAsMention() + ".").queue();
							});
						} else {
							channel.sendMessage("**No member was found by the identifier** `" + args[1] + "`").queue();
						}
					}
				} else {
					channel.sendMessage("**Invalid Amount** `" + args[0] + "`").queue();
				}
			} else {
				channel.sendMessage("**You cannot delete messages.**").queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}
