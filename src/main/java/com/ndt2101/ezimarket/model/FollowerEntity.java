package com.ndt2101.ezimarket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @ManyToOne()
    @JoinColumn(name="from_user")
    private UserLoginDataEntity from;

    @ManyToOne()
    @JoinColumn(name="to_shop")
    private ShopEntity to;

    @Column()
    @CreationTimestamp
    private Date followedDate;

//    @Column
//    @UpdateTimestamp
//    private Date unfollowedDate;
}
