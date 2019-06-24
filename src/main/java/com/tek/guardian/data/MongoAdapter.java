package com.tek.guardian.data;

import com.mongodb.MongoClient;

import dev.morphia.Datastore;
import dev.morphia.Morphia;

public class MongoAdapter {
	
	private final Morphia morphia = new Morphia();
	private MongoClient client;
	private Datastore datastore;
	
	public void connect(String database) {
		client = new MongoClient();
		morphia.mapPackage("com.tek.guardian.data");
		datastore = morphia.createDatastore(client, database);
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
