package com.btto.core.service;

import com.btto.core.controller.ApiException;
import com.btto.core.dao.RoomDao;
import com.btto.core.domain.Company;
import com.btto.core.domain.Office;
import com.btto.core.domain.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
public class RoomServiceImpl extends AbstractEntityServiceImpl<Room, RoomDao> implements RoomService {

    private final OfficeService officeService;

    @Autowired
    public RoomServiceImpl(RoomDao dao, OfficeService officeService) {
        super(dao);
        this.officeService = officeService;
    }

    @Override
    @Transactional
    public Room create(@NotNull Company userCompany, @NotNull Integer officeId, String name, String level) {
        final Room room = new Room();
        room.setOffice(getOffice(officeId));
        room.setName(name);
        room.setLevel(level);

        return dao.merge(room);
    }

    @Override
    @Transactional
    public Room update(Integer id, @Nullable String name, @Nullable String level) {
        final Room office = getRoom(id);

        if (level != null) {
            office.setLevel(level);
        }

        if (name != null) {
            office.setName(name);
        }

        return dao.merge(office);
    }

    @Override
    @Transactional
    public void delete(final Integer id) {
        dao.deleteById(id);
    }

    private Room getRoom(final Integer roomId) {
        return Optional.ofNullable(dao.findOne(roomId))
                .orElseThrow(() -> new ApiException("Can't find room " + roomId, HttpStatus.NOT_FOUND));
    }

    private Office getOffice(final Integer officeId) {
        return officeService.find(officeId)
                .orElseThrow(() -> new ApiException("Can't find office " + officeId, HttpStatus.NOT_FOUND));
    }
}
