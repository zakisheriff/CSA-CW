package org.westminster.smartcampus.resource;

import org.westminster.smartcampus.exception.RoomNotEmptyException;
import org.westminster.smartcampus.model.Room;
import org.westminster.smartcampus.service.InventoryService;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

/**
 * Resource for managing /rooms path.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final InventoryService inventory = InventoryService.getInstance();

    @GET
    public List<Room> getAllRooms(@Context UriInfo uriInfo) {
        List<Room> rooms = inventory.getAllRooms();
        List<Room> responseList = new java.util.ArrayList<>();
        
        for (Room room : rooms) {
            // Defensive copy for thread-safe link population
            Room copy = new Room(room.getId(), room.getName(), room.getCapacity());
            copy.setSensorIds(new java.util.ArrayList<>(room.getSensorIds()));
            
            String roomUri = uriInfo.getAbsolutePathBuilder().path(copy.getId()).build().toString();
            copy.getLinks().put("self", roomUri);
            copy.getLinks().put("sensors", roomUri + "/sensors");
            responseList.add(copy);
        }
        return responseList;
    }

    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        // Professional validation layer
        org.westminster.smartcampus.service.ValidationService.validateRoom(room);
        
        // Bug Fix: Prevent silent overwrites on POST (REST standard)
        if (inventory.getRoom(room.getId()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Room with ID " + room.getId() + " already exists. Use PUT for updates.")
                    .build();
        }
        
        inventory.addRoom(room);
        
        // HATEOAS: Populate links in the creation response for immediate client use
        String roomUri = uriInfo.getAbsolutePathBuilder().path(room.getId()).build().toString();
        room.getLinks().clear();
        room.getLinks().put("self", roomUri);
        room.getLinks().put("sensors", roomUri + "/sensors");
        
        // 201 Created with Location header
        return Response.created(URI.create(roomUri)).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId, @Context UriInfo uriInfo) {
        Room room = inventory.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        // Defensive copy for thread-safe link population
        Room copy = new Room(room.getId(), room.getName(), room.getCapacity());
        copy.setSensorIds(new java.util.ArrayList<>(room.getSensorIds()));
        
        // HATEOAS: Populate dynamic self-link on the copy
        copy.getLinks().put("self", uriInfo.getAbsolutePath().toString());
        copy.getLinks().put("sensors", uriInfo.getAbsolutePathBuilder().path("sensors").build().toString());
        
        return Response.ok(copy).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = inventory.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        boolean deleted = inventory.deleteRoom(roomId);
        if (!deleted) {
            // Throw custom exception to be handled by ExceptionMapper (409 Conflict)
            throw new RoomNotEmptyException("Room " + roomId + " cannot be deleted because it contains sensors.");
        }
        
        return Response.noContent().build();
    }
}
