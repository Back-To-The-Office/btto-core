package com.btto.core.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Participant {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "participant", nullable = false)
    private User participant;
    @ManyToOne
    @JoinColumn(name = "department", nullable = false)
    private Department department;
}
