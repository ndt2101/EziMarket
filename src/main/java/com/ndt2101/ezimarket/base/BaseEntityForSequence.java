package com.ndt2101.ezimarket.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseEntityForSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(updatable = false)
    private Long createdBy;

    @Column(updatable = false)
    @CreationTimestamp
    private Date createdTime;

    @Column
    private Long updatedBy;

    @Column
    @UpdateTimestamp
    private Date updatedTime;
}
