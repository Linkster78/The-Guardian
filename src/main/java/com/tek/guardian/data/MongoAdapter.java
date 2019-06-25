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
	
	public void removeTemporaryAction(TemporaryAction action) {
		datastore.delete(action);
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
	
	public void removeCustomVoiceChannel(CustomVoiceChannel channel) {
		datastore.delete(channel);
	}
	
	public void saveCustomVoiceChannel(CustomVoiceChannel channel) {
		datastore.save(channel);
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
