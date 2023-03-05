package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "comment")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserLoginDataEntity user;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CommentEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommentEntity> children = new ArrayList<>();
}
