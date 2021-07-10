package com.btto.core.controller.model;

import com.btto.core.domain.Office;
import lombok.Value;

import java.time.Instant;

@Value
public class OfficeModel {
    int id;
    String name;
    String address;
    Instant createdAt;
    Instant modifiedAt;

    public static OfficeModel fromOffice(Office office) {
        return new OfficeModel(office.getId(),
                office.getName(),
                office.getAddress(),
                office.getCreatedAt(),
                office.getModifiedAt());
    }
}
