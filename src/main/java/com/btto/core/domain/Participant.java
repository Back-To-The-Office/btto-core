package com.btto.core.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Participant {
    @Id
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "participant", nullable = false)
    private User participant;
    @ManyToOne
    @JoinColumn(name = "department", nullable = false)
    private Department department;
}
