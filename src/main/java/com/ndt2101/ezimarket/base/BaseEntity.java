package com.ndt2101.ezimarket.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    @JsonIgnore
    private Long createdBy;

    @Column(updatable = false)
    @CreationTimestamp
    @JsonIgnore
    private Date createdTime;

    @Column
    @JsonIgnore
    private Long updatedBy;

    @Column
    @UpdateTimestamp
    @JsonIgnore
    private Date updatedTime;
}