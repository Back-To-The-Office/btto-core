package com.btto.core.service;

import com.btto.core.domain.Department;
import com.btto.core.domain.User;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.List;

public interface DepartmentService extends AbstractEntityService<Department> {

    Integer create(String name, User creator);

    void delete(Integer departmentId);

    Department update(Integer departmentId, @Nullable String name);

    Department assign(Integer departmentId, Integer ownerId);

    void addParticipant(Integer departmentId, Integer participantId);

    @Transactional
    void removeParticipant(Integer departmentId, Integer participantId);

    @Transactional
    List<Department> getUserCompanyDepartments(User user);
}
