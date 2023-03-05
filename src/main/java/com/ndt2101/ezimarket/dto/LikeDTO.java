package com.ndt2101.ezimarket.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LikeDTO {
    private Long postId;
    private Long userId;
    private int likeNums;
    private boolean liked;
}
