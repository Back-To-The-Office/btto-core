package com.btto.core.dao;

import com.btto.core.domain.Desk;
import com.btto.core.domain.Room;
import java.util.List;

public interface DeskDao extends AbstractJpaDao<Desk> {
    List<Desk> finalAllDesksOfRoom(Room room);
}
