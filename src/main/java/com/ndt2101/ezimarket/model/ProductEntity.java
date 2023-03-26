package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Table(name = "Product")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductEntity extends BaseEntity {
    @Column
    @NotBlank
    private String name;
    @Column
    @NotBlank
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;
    @Column
    @NotBlank
    private String status;
    @Column
    private Long view;
    @Column
    private Float rate;
    @Column
    private Long soldNumber;
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private ShopEntity shop;
    @Column
    private float weight;
    @Column
    private float width;
    @Column
    private float height;
    @Column
    private float length;
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST,CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<ProductTypeEntity> productTypes;
    @OneToMany(mappedBy = "product")
    private List<ImageEntity> imageEntities;
    @ManyToOne
    @JoinColumn(name = "sale_program_id")
    private SaleProgramEntity saleProgram;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PostEntity> posts;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewEntity> reviews;

    public ProductEntity clone(ProductEntity productEntity) {
        ProductEntity product = new ProductEntity();
        product.setId(productEntity.getId());
        product.setCreatedBy(productEntity.getCreatedBy());
        product.setCreatedTime(productEntity.getCreatedTime());
        product.setUpdatedBy(productEntity.getUpdatedBy());
        product.setUpdatedTime(productEntity.getUpdatedTime());
        product.setName(productEntity.getName());
        product.setDescription(productEntity.getDescription());
        product.setCategory(productEntity.getCategory());
        product.setStatus(productEntity.getStatus());
        product.setView(productEntity.getView());
        product.setRate(productEntity.getRate());
        product.setSoldNumber(productEntity.getSoldNumber());
        product.setShop(productEntity.getShop());
        product.setWeight(productEntity.getWeight());
        product.setWidth(productEntity.getWidth());
        product.setHeight(productEntity.getHeight());
        product.setLength(productEntity.getLength());
        product.setProductTypes(productEntity.getProductTypes());
        product.setImageEntities(productEntity.getImageEntities());
        product.setSaleProgram(productEntity.getSaleProgram());
        product.setPosts(productEntity.getPosts());
        product.setReviews(productEntity.getReviews());
        return product;
    }
}
