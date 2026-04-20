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
    public List<Room> getAllRooms() {
        return inventory.getAllRooms();
    }

    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Room ID is required").build();
        }
        inventory.addRoom(room);
        
        // 201 Created with Location header
        URI uri = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        return Response.created(uri).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = inventory.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(room).build();
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
