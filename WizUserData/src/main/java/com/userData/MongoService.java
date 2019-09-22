package com.userData;

import java.util.ArrayList;
import static com.mongodb.client.model.Filters.*;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

public class MongoService {

	public List<Document> find(MongoCollection<Document> collection) {
		return collection.find().into(new ArrayList<>());
	}

	public void insertOne(MongoCollection<Document> collection, Document document) {
		collection.insertOne(document);

	}

	public List<Document> findByKey(MongoCollection<Document> collection, String string, String firstName) {

		return collection.find(eq(string, firstName)).into(new ArrayList<>());
	}

	public void updateOnePassword(MongoCollection<Document> collection, String key1, String key2, String key3,
			UserDetails user) {
		collection.updateOne(new Document(key1, user.getFirstName()).append(key2, user.getJob()),
				new Document("$set", new Document(key3, user.getPassword())));
	}

}
