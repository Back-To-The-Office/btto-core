package com.btto.core.service;

import com.btto.core.domain.Company;
import com.btto.core.domain.Room;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public interface RoomService extends AbstractEntityService<Room> {

    @Transactional
    Room create(@NotNull Company company, @NotNull Integer officeId, String name, String level);

    @Transactional
    Room update(Integer id, @Nullable String name, @Nullable String level);

    @Transactional
    void delete(Integer id);
}
