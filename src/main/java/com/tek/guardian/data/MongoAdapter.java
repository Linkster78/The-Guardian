package com.tek.guardian.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.mongodb.MongoClient;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;
import net.dv8tion.jda.api.entities.Guild;

public class MongoAdapter {
	
	private final Morphia morphia = new Morphia();
	private MongoClient client;
	private Datastore datastore;
	
	public void connect(String database) {
		client = new MongoClient();
		morphia.mapPackage("com.tek.guardian.data");
		datastore = morphia.createDatastore(client, database);
	}
	
	public ServerProfile createServerProfile(Guild guild) {
		ServerProfile newProfile = new ServerProfile(guild.getId());
		datastore.save(newProfile);
		newProfile.join(guild);
		return newProfile;
	}
	
	public void removeServerProfile(Guild guild) {
		Query<ServerProfile> profileQuery = datastore.createQuery(ServerProfile.class)
				.field("serverId").equal(guild.getId());
		if(profileQuery.count() > 0) {
			ServerProfile profile = profileQuery.first();
			datastore.delete(profile);
			profile.leave(guild);
		}
	}
	
	public ServerProfile getServerProfile(Guild guild) {
		Query<ServerProfile> profileQuery = datastore.createQuery(ServerProfile.class)
				.field("serverId").equal(guild.getId());
		if(profileQuery.count() > 0) {
			ServerProfile first = profileQuery.first();
			first.verify(guild);
			return first;
		}
		return createServerProfile(guild);
	}
	
	public void saveServerProfile(ServerProfile serverProfile) {
		datastore.save(serverProfile);
	}
	
	public List<TemporaryAction> getTemporaryActions() {
		Query<TemporaryAction> actionQuery = datastore.createQuery(TemporaryAction.class);
		ArrayList<TemporaryAction> actionList = new ArrayList<TemporaryAction>((int) actionQuery.count());
		Iterator<TemporaryAction> actionIterator = actionQuery.iterator();
		while(actionIterator.hasNext()) {
			actionList.add(actionIterator.next());
		}
		return actionList;
	}
	
	public void removeGuildTemporaryActions(String guildId) {
		Query<TemporaryAction> actionQuery = datastore.createQuery(TemporaryAction.class)
				.field("guildId").equal(guildId);
		datastore.delete(actionQuery);
	}
	
	public void removeTemporaryAction(TemporaryAction action) {
		Query<TemporaryAction> actionQuery = datastore.createQuery(TemporaryAction.class)
				.field("objectId").equal(action.getObjectId());
		datastore.delete(actionQuery);
	}
	
	public void createTemporaryAction(TemporaryAction action) {
		datastore.save(action);
	}
	
	public List<CustomVoiceChannel> getCustomVoiceChannels() {
		Query<CustomVoiceChannel> customVoiceChannelQuery = datastore.createQuery(CustomVoiceChannel.class);
		ArrayList<CustomVoiceChannel> customVoiceChannelList = new ArrayList<CustomVoiceChannel>((int) customVoiceChannelQuery.count());
		Iterator<CustomVoiceChannel> customVoiceChannelIterator = customVoiceChannelQuery.iterator();
		while(customVoiceChannelIterator.hasNext()) {
			customVoiceChannelList.add(customVoiceChannelIterator.next());
		}
		return customVoiceChannelList;
	}
	
	public Optional<CustomVoiceChannel> getCustomVoiceChannel(String guildId, String userId) {
		Query<CustomVoiceChannel> channelQuery = datastore.createQuery(CustomVoiceChannel.class)
				.field("userId").equal(userId)
				.field("guildId").equal(guildId);
		if(channelQuery.count() > 0) {
			return Optional.of(channelQuery.first());
		} else {
			return Optional.empty();
		}
	}
	
	public Optional<CustomVoiceChannel> getCustomVoiceChannel(String channelId) {
		Query<CustomVoiceChannel> channelQuery = datastore.createQuery(CustomVoiceChannel.class)
				.field("channelId").equal(channelId);
		if(channelQuery.count() > 0) {
			return Optional.of(channelQuery.first());
		} else {
			return Optional.empty();
		}
	}
	
	public void removeGuildCustomVoiceChannels(String guildId) {
		Query<CustomVoiceChannel> channelQuery = datastore.createQuery(CustomVoiceChannel.class)
				.field("guildId").equal(guildId);
		datastore.delete(channelQuery);
	}
	
	public void removeCustomVoiceChannel(CustomVoiceChannel channel) {
		Query<CustomVoiceChannel> channelQuery = datastore.createQuery(CustomVoiceChannel.class)
				.field("id").equal(channel.getId());
		datastore.delete(channelQuery);
	}
	
	public void saveCustomVoiceChannel(CustomVoiceChannel channel) {
		datastore.save(channel);
	}
	
	public List<ReactionRole> getReactionRoles() {
		Query<ReactionRole> roleQuery = datastore.createQuery(ReactionRole.class);
		ArrayList<ReactionRole> roleList = new ArrayList<ReactionRole>((int) roleQuery.count());
		Iterator<ReactionRole> roleIterator = roleQuery.iterator();
		while(roleIterator.hasNext()) {
			roleList.add(roleIterator.next());
		}
		return roleList;
	}
	
	public List<ReactionRole> getReactionRoles(String messageId) {
		Query<ReactionRole> roleQuery = datastore.createQuery(ReactionRole.class)
				.field("messageId").equal(messageId);
		ArrayList<ReactionRole> roleList = new ArrayList<ReactionRole>((int) roleQuery.count());
		Iterator<ReactionRole> roleIterator = roleQuery.iterator();
		while(roleIterator.hasNext()) {
			roleList.add(roleIterator.next());
		}
		return roleList;
	}
	
	public List<ReactionRole> getReactionRoles(String messageId, String emoteId) {
		Query<ReactionRole> roleQuery = datastore.createQuery(ReactionRole.class)
				.field("messageId").equal(messageId)
				.field("emoteId").equal(emoteId);
		ArrayList<ReactionRole> roleList = new ArrayList<ReactionRole>((int) roleQuery.count());
		Iterator<ReactionRole> roleIterator = roleQuery.iterator();
		while(roleIterator.hasNext()) {
			roleList.add(roleIterator.next());
		}
		return roleList;
	}
	
	public void removeGuildReactionRoles(String guildId) {
		Query<ReactionRole> roleQuery = datastore.createQuery(ReactionRole.class)
				.field("guildId").equal(guildId);
		datastore.delete(roleQuery);
	}
	
	public void removeReactionRole(ReactionRole role) {
		Query<ReactionRole> roleQuery = datastore.createQuery(ReactionRole.class)
				.field("objectId").equal(role.getObjectId());
		datastore.delete(roleQuery);
	}
	
	public List<RoleMemory> getRoleMemories() {
		Query<RoleMemory> memoryQuery = datastore.createQuery(RoleMemory.class);
		ArrayList<RoleMemory> memoryList = new ArrayList<RoleMemory>((int) memoryQuery.count());
		Iterator<RoleMemory> memoryIterator = memoryQuery.iterator();
		while(memoryIterator.hasNext()) {
			memoryList.add(memoryIterator.next());
		}
		return memoryList;
	}
	
	public Optional<RoleMemory> getRoleMemory(String guildId, String userId) {
		Query<RoleMemory> memoryQuery = datastore.createQuery(RoleMemory.class)
				.field("userId").equal(userId)
				.field("guildId").equal(guildId);
		if(memoryQuery.count() > 0) {
			return Optional.of(memoryQuery.first());
		} else {
			return Optional.empty();
		}
	}
	
	public void removeGuildRoleMemory(String guildId) {
		Query<RoleMemory> memoryQuery = datastore.createQuery(RoleMemory.class)
				.field("guildId").equal(guildId);
		datastore.delete(memoryQuery);
	}
	
	public void removeRoleMemory(RoleMemory memory) {
		Query<RoleMemory> memoryQuery = datastore.createQuery(RoleMemory.class)
				.field("objectId").equal(memory.getObjectId());
		datastore.delete(memoryQuery);
	}
	
	public void saveRoleMemory(RoleMemory memory) {
		datastore.save(memory);
	}
	
	public void saveReactionRole(ReactionRole role) {
		datastore.save(role);
	}
	
	public Morphia getMorphia() {
		return morphia;
	}
	
	public MongoClient getClient() {
		return client;
	}
	
	public Datastore getDatastore() {
		return datastore;
	}
	
}
