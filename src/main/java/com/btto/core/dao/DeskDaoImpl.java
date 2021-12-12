package com.btto.core.dao;

import com.btto.core.domain.Desk;
import com.btto.core.domain.Room;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeskDaoImpl extends AbstractJpaDaoImpl<Desk> implements DeskDao {

    public DeskDaoImpl() {
        super(Desk.class);
    }

    @Override
    public List<Desk> finalAllDesksOfRoom(Room room) {
        return entityManager.createQuery("from " + Desk.class.getName() + "d where d.room = :room", Desk.class)
                .setParameter("room", room)
                .getResultList();
    }
}
