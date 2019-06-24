package com.tek.guardian.data;

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
		if(profileQuery.count() > 0) return profileQuery.first();
		return createServerProfile(guild);
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
