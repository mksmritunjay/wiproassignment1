package com.userData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

@Path("/user")
public class UserDataController {

	private MongoCollection<Document> collection;
	private MongoService mongoService;

	public UserDataController(MongoCollection<Document> collection2, MongoService mongoService2) {
		this.collection = collection2;
		this.mongoService = mongoService2;

	}

	// @RoleAllowd is used to admin purpose
	@RolesAllowed({ "ADMIN" })
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all")
	public Response getAllUsers() {
		List<Document> documents = mongoService.find(collection);
		return Response.ok(documents).build();
	}

	@RolesAllowed({ "ADMIN" })
	@POST
	@Path("/insertNewUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response insertNewUser(@NotNull @Valid final UserDetails userDetail) {
		Gson gson = new Gson();
		String json = gson.toJson(userDetail);
		mongoService.insertOne(collection, new Document(BasicDBObject.parse(json)));// bson data mae convert ho rha
		Map<String, String> response = new HashMap<>();
		response.put("message", "User Get Added");
		return Response.ok(response).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/loginUser")
	public Response login(@NotNull @Valid final UserDetails user) {
		List<Document> documents = mongoService.findByKey(collection, "firstName", user.getFirstName());

		boolean found = false;

		for (Document emp : documents) {
			found = emp.containsValue(user.getPassword());

		}

		Map<String, List<Document>> response = new HashMap<>();
		Map<String, String> res = new HashMap<>();
//		response.put("Message", found ? "Sucess" : "Fail");
//		return Response.ok(response).build();

		response.put("data", documents);
		res.put("message", "login failed");

		return Response.ok(found ? response : res).build();
	}
	// this one is use to find
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("{name}")
//	public Response getUser(@PathParam("name") final String name) {
//		List<Document> documents=mongoService.findByKey(collection,"firstName",name);
//		return Response.ok(documents).build();
//	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    @Path("/forgotPassword")
    public Response forgotPassword(@NotNull @Valid final UserDetails user) {
        mongoService.updateOnePassword(collection, "firstName", "job", "password", user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password updated successfully");
        return Response.ok(response).build();
    }

}
