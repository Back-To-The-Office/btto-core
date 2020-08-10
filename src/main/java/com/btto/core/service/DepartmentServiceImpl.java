package com.btto.core.service;

import com.btto.core.dao.DepartmentDao;
import com.btto.core.dao.ParticipantDao;
import com.btto.core.domain.Company;
import com.btto.core.domain.Department;
import com.btto.core.domain.Participant;
import com.btto.core.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Optional;

@Service
public class DepartmentServiceImpl extends AbstractEntityServiceImpl<Department, DepartmentDao> implements DepartmentService {

    private final ParticipantDao participantDao;
    private final UserService userService;

    @Autowired
    public DepartmentServiceImpl(final DepartmentDao departmentDao, ParticipantDao participantDao, UserService userService) {
        super(departmentDao);
        this.participantDao = participantDao;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void create(final String name, final User creator) {
        final Company company = creator.getCompany()
                .orElseThrow(() -> new ServiceException(
                        "User " + creator.getId() + " doesn't have a company",
                        ServiceException.Type.INVALID_STATE)
                );

        final Department department = new Department();
        department.setCompany(company);
        department.setOwner(creator);
        department.setName(name);
        dao.create(department);

        final Participant participant = new Participant();
        participant.setDepartment(department);
        participant.setParticipant(creator);

        participantDao.create(participant);
    }

    @Override
    @Transactional
    public void delete(final Integer departmentId) {
        dao.deleteById(departmentId);
    }

    @Override
    @Transactional
    public Department update(final Integer departmentId, final @Nullable String name) {
        final Department department = getDepartment(departmentId);

        if (StringUtils.isNotBlank(name)) {
            department.setName(name);
        }

        return dao.update(department);
    }

    @Override
    @Transactional
    public Department assign(final Integer departmentId, final Integer ownerId) {
        final Department department = getDepartment(departmentId);

        department.setOwner(getUser(ownerId));

        return dao.update(department);
    }

    @Override
    @Transactional
    public void addParticipant(final Integer departmentId, final Integer participantId) {
        final User participant = getUser(participantId);
        final Department department = getDepartment(departmentId);

        final Participant participantRecord = new Participant();
        participantRecord.setDepartment(department);
        participantRecord.setParticipant(participant);

        participantDao.create(participantRecord);
    }


    private Department getDepartment(final Integer departmentId) {
        return Optional.ofNullable(dao.findOne(departmentId))
                .orElseThrow(() -> new ServiceException("Can't find department with id: " + departmentId, ServiceException.Type.NOT_FOUND));
    }

    private User getUser(final Integer userId) {
        return userService.find(userId).orElseThrow(
                () -> new ServiceException("Can't find user with id: " + userId, ServiceException.Type.NOT_FOUND));
    }
}
