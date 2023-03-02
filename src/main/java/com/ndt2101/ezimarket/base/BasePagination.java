package com.ndt2101.ezimarket.base;

import com.ndt2101.ezimarket.constant.Common;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.dto.pagination.Pagination;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;
@Component
public class BasePagination<E, R extends JpaRepository<E, ?> & JpaSpecificationExecutor<E>> {

    private R repository;

    public BasePagination() {
    }

    public BasePagination(R repository) {
        this.repository = repository;
    }

    public PaginateDTO<E> paginate(Integer page, Integer perPage) {
        if (page == null || page <= 0) {
            page = 1;
        }
        if (perPage == null || perPage <= 0) {
            perPage = Common.PAGING_DEFAULT_LIMIT;
        }
        Page<E> pageData = repository.findAll(PageRequest.of(page - 1, perPage));

        Pagination pagination = new Pagination(page, perPage, pageData.getTotalPages(), pageData.getTotalElements());
        return new PaginateDTO<>(pageData, pagination);
    }

    public PaginateDTO<E> paginate(Integer page, Integer perPage, GenericSpecification<E> specification) {
        if (page == null || page <= 0) {
            page = 1;
        }
        if (perPage == null || perPage <= 0) {
            perPage = Common.PAGING_DEFAULT_LIMIT;
        }
        Page<E> pageData = repository.findAll(specification, PageRequest.of(page - 1, perPage, specification.getSort()));
        Pagination pagination = new Pagination(page, perPage, pageData.getTotalPages(), pageData.getTotalElements());
        return new PaginateDTO<>(pageData, pagination);
    }
}

