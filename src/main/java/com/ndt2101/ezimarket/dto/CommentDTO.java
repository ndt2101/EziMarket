package com.ndt2101.ezimarket.dto;

import com.ndt2101.ezimarket.base.BaseDTO;
import com.ndt2101.ezimarket.model.CommentEntity;
import com.ndt2101.ezimarket.model.PostEntity;
import com.ndt2101.ezimarket.model.UserLoginDataEntity;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommentDTO extends BaseDTO {
    private Long postId;
    private String content;
    private UserDTO user;
    private Long parentId;
    private List<CommentDTO> children = new ArrayList<>();
    private Date createTime;
}
