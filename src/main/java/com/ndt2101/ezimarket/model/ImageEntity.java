package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Table(name = "Image")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ImageEntity extends BaseEntity {
    @Column
    @NotBlank
    private String name;

    @Column
    @NotBlank
    private String url;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @OneToOne(mappedBy = "image", cascade = CascadeType.ALL)
    private PostEntity post;
}
