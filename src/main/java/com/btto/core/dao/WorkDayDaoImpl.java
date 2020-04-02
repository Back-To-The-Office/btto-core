package com.btto.core.dao;

import com.btto.core.domain.WorkDay;
import org.springframework.stereotype.Component;

@Component
public class WorkDayDaoImpl extends AbstractJpaDaoImpl<WorkDay> implements WorkDayDao {

    public WorkDayDaoImpl() {
        super(WorkDay.class);
    }
}
