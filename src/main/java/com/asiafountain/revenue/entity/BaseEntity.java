package com.asiafountain.revenue.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@MappedSuperclass
@NoArgsConstructor
@Setter
@Getter
public class BaseEntity {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="status")
    private String status = "active";

    @Column(name="created_on", insertable = false, updatable = false)
    private Date createdOn;

    @Column(name="updated_on", insertable = false, updatable = false)
    private Date updatedOn;

}
