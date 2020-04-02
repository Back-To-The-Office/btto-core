package com.btto.core.domain;

import com.btto.core.domain.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    @Lob
    private String contacts;
    @Enumerated(EnumType.STRING)
    private Role role;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Department> departments;
    @OneToMany(mappedBy = "participant", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Participant> participants;
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<WorkDay> workDays;
    private Instant lastUpdate;
    private String deactivatedEmail;

    public Optional<Company> getCompany() {
        return Optional.ofNullable(company);
    }

    public Optional<String> getLastName() {
        return Optional.of(lastName);
    }

    public Optional<String> getContacts() {
        return Optional.of(contacts);
    }

    public Optional<String> getDeactivatedEmail() {
        return Optional.of(deactivatedEmail);
    }
}
