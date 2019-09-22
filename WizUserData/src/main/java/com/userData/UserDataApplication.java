package com.userData;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class UserDataApplication extends Application<UserDataConfiguration> {

	public static void main(final String[] args) throws Exception {
		new UserDataApplication().run("server", "config.yml");

	}

	@Override
	public String getName() {
		return "UserData";
	}

	@Override
	public void initialize(final Bootstrap<UserDataConfiguration> bootstrap) {
		// TODO: application initialization
	}

	@Override
	public void run(final UserDataConfiguration config, final Environment env) {
		MongoClient mongoClient = new MongoClient(config.getMongoHost(), config.getMongoPort());
		MongoManaged mongoManaged = new MongoManaged(mongoClient);
		env.lifecycle().manage(mongoManaged);
		MongoDatabase db = mongoClient.getDatabase(config.getMongoDB());
		MongoCollection<Document> collection = db.getCollection(config.getCollectionName());

		env.jersey().register(new UserDataController(collection, new MongoService()));

		env.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
				.setAuthenticator(new AppBasicAuthenticator())
				.setAuthorizer(new AppAuthorizer()).setRealm("BASIC-AUTH-REALM")
				.buildAuthFilter()));

	}

}
