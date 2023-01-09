package com.ndt2101.ezimarket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "followers")
@NoArgsConstructor
@AllArgsConstructor
//@Data
@Getter
@Setter
@SuperBuilder
public class FollowerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="from_user_fk")
    private UserLoginDataEntity from;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="to_user_fk")
    private UserLoginDataEntity to;

    @Column()
    private Date followedDate;

    @Column
    private Date unfollowedDate;
}
