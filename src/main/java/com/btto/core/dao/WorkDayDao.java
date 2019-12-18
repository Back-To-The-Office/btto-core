package com.btto.core.dao;

import com.btto.core.domain.WorkDay;
import org.springframework.stereotype.Component;

@Component
public class WorkDayDao extends AbstractJpaDao<WorkDay>{

    public WorkDayDao() {
        super(WorkDay.class);
    }
}
