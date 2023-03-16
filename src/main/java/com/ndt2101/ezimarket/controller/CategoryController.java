package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.CategoryDTO;
import com.ndt2101.ezimarket.model.CategoryEntity;
import com.ndt2101.ezimarket.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/category/")
public class CategoryController extends BaseController<CategoryDTO> {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper mapper;

    @GetMapping
    public ResponseEntity<?> getCategories() {
        List<CategoryEntity> categoryEntities = categoryRepository.findAll();
        List<CategoryDTO> result = categoryEntities.stream().map(categoryEntity -> {
            CategoryDTO categoryDTO = mapper.map(categoryEntity, CategoryDTO.class);
            if (categoryEntity.getParent() != null) {
                categoryDTO.setParentId(categoryEntity.getParent().getId());
            }
            return categoryDTO;
        }).toList();
        return successfulListResponse(result);
    }
}
