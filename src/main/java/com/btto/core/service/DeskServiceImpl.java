package com.btto.core.service;

import com.btto.core.controller.ApiException;
import com.btto.core.dao.DeskDao;
import com.btto.core.domain.Desk;
import com.btto.core.domain.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Service
public class DeskServiceImpl extends AbstractEntityServiceImpl<Desk, DeskDao> implements DeskService {

    private final RoomService roomService;

    @Autowired
    public DeskServiceImpl(DeskDao dao, RoomService roomService) {
        super(dao);
        this.roomService = roomService;
    }

    @Override
    @Transactional
    public Desk create(int roomId, String name, Integer capacity) {
        final Desk desk = new Desk();
        desk.setRoom(roomService.find(roomId)
                .orElseThrow(() -> new ApiException("Can't find room " + roomId, HttpStatus.NOT_FOUND)));
        desk.setName(name);
        desk.setCapacity(capacity);

        return dao.merge(desk);
    }

    @Override
    @Transactional
    public Desk update(int id, @Nullable String name, int capacity) {
        final Desk desk = Optional.ofNullable(dao.findOne(id))
                .orElseThrow(() -> new ApiException("Can't find desk " + id, HttpStatus.NOT_FOUND));
        desk.setName(name);
        desk.setCapacity(capacity);

        return dao.merge(desk);
    }

    @Override
    @Transactional
    public void delete(final int id) {
        dao.deleteById(id);
    }

    @Override
    @Transactional
    public List<Desk> finalAllDesksOfRoom(int roomId) {
        Room room = roomService.find(roomId)
                .orElseThrow(() -> new ApiException("Can't find room " + roomId, HttpStatus.NOT_FOUND));

        return dao.finalAllDesksOfRoom(room);
    }
}
