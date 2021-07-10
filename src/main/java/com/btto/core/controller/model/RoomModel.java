package com.btto.core.controller.model;

import com.btto.core.domain.Room;
import lombok.Value;

@Value
public class RoomModel {
    int id;
    String level;
    String name;

    public static RoomModel fromRoom(final Room room) {
        return new RoomModel(room.getId(), room.getLevel(), room.getName());
    }
}
