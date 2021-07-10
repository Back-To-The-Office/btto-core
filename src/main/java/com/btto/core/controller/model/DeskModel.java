package com.btto.core.controller.model;

import com.btto.core.domain.Desk;
import lombok.Value;

@Value
public class DeskModel {
    int id;
    String name;
    int capacity;

    public static DeskModel fromDesk(Desk desk) {
        return new DeskModel(desk.getId(), desk.getName(), desk.getCapacity());
    }
}
