package com.btto.core.domain;

import com.btto.core.domain.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
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
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Users")
public class User implements EntityWithId {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @Length(min = 3, max = 255)
    private String email;
    @Length(max = 255)
    private String firstName;
    @Length(max = 255)
    private String lastName;
    private String password;
    @Lob
    private String contacts;
    @Enumerated(EnumType.STRING)
    private Role role;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private Company company;
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Department> departments;
    @OneToMany(mappedBy = "participant", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Participant> participants;
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<WorkSession> workSessions;
    private Instant lastUpdate;
    @Length(min = 3, max = 255)
    private String deactivatedEmail;
    @Convert(converter = Jsr310JpaConverters.ZoneIdConverter.class)
    private ZoneId timezone;
    @Length(max = 255)
    private String position;

    public Optional<Company> getCompany() {
        return Optional.ofNullable(company);
    }

    public Optional<String> getLastName() {
        return Optional.ofNullable(lastName);
    }

    public Optional<String> getContacts() {
        return Optional.ofNullable(contacts);
    }

    public Optional<String> getDeactivatedEmail() {
        return Optional.ofNullable(deactivatedEmail);
    }

    public Optional<String> getPosition() {
        return Optional.ofNullable(position);
    }
}
