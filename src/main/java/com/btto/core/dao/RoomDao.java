package com.btto.core.dao;

import com.btto.core.domain.Office;
import com.btto.core.domain.Room;

import java.util.List;

public interface RoomDao extends AbstractJpaDao<Room> {
    List<String> findOfficeLevels(Office office);
}
