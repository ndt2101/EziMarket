package com.ndt2101.ezimarket.dto;

import com.ndt2101.ezimarket.base.BaseDTO;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostDTO extends BaseDTO {
    private ShopDTO shop;
    private String postContentText;
    private String imageUrl;
    private ProductResponseDTO product;
    private VoucherDTO voucher;
    private LikeDTO likes;
    private Long createdTime;
    private Long commentQuantity;
}
