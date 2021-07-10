package com.btto.core.dao;

import com.btto.core.domain.Office;
import com.btto.core.domain.Room;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomDaoImpl extends AbstractJpaDaoImpl<Room> implements RoomDao {

    public RoomDaoImpl() {
        super(Room.class);
    }

    @Override
    public List<String> findOfficeLevels(Office office) {
        return entityManager.createQuery("select distinct r.level from " + Room.class.getName() + " r where r.office = :office", String.class)
                .setParameter("office", office)
                .getResultList();
    }
}
