package com.ndt2101.ezimarket.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FileDTO {
    private MultipartFile file;
}
