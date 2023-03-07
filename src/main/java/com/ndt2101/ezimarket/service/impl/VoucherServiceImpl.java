package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.base.BasePagination;
import com.ndt2101.ezimarket.dto.SaleProgramDTO;
import com.ndt2101.ezimarket.dto.VoucherDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.SaleProgramEntity;
import com.ndt2101.ezimarket.model.ShopEntity;
import com.ndt2101.ezimarket.model.UserLoginDataEntity;
import com.ndt2101.ezimarket.model.VoucherEntity;
import com.ndt2101.ezimarket.repository.SaleProgramRepository;
import com.ndt2101.ezimarket.repository.ShopRepository;
import com.ndt2101.ezimarket.repository.UserRepository;
import com.ndt2101.ezimarket.repository.VoucherRepository;
import com.ndt2101.ezimarket.service.VoucherService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;

@Service
public class VoucherServiceImpl extends BasePagination<VoucherEntity, VoucherRepository> implements VoucherService {
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private VoucherRepository voucherRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VoucherServiceImpl(VoucherRepository voucherRepository) {
        super(voucherRepository);
    }
    @Override
    public VoucherDTO create(VoucherDTO voucherDTO) {
        ShopEntity shop = shopRepository.findById(voucherDTO.getShopId()).orElseThrow(() -> new NotFoundException("Shop not found"));
        VoucherEntity savedVoucher = mapper.map(voucherDTO, VoucherEntity.class);
        savedVoucher.setShop(shop);
        savedVoucher = voucherRepository.save(savedVoucher);
        voucherDTO = mapper.map(savedVoucher, VoucherDTO.class);
        voucherDTO.setShopId(shop.getId());
        voucherDTO.setImg(shop.getUserLoginData().getAvatarUrl());
        return voucherDTO;
    }

    @Override
    public VoucherDTO update(VoucherDTO voucherDTO, Long id) {
        ShopEntity shop = shopRepository.findById(voucherDTO.getShopId()).orElseThrow(() -> new NotFoundException("Shop not found"));
        VoucherEntity currentVoucher = voucherRepository.findById(id).orElseThrow(() -> new NotFoundException("Voucher not found"));

        VoucherEntity updateVoucher = mapper.map(voucherDTO, VoucherEntity.class);
        Set<UserLoginDataEntity> userHasVouchers = currentVoucher.getUsers();
        mapper.map(updateVoucher, currentVoucher);
        currentVoucher.setShop(shop);
        currentVoucher.setUsers(userHasVouchers);

        updateVoucher = voucherRepository.save(currentVoucher);
        voucherDTO = mapper.map(updateVoucher, VoucherDTO.class);
        voucherDTO.setShopId(shop.getId());
        voucherDTO.setImg(shop.getUserLoginData().getAvatarUrl());
        return voucherDTO;
    }

    @Override
    public String delete(Long id) {
        VoucherEntity voucherEntity = voucherRepository.findById(id).orElseThrow(() -> new NotFoundException("Voucher not found"));
        Set<UserLoginDataEntity> users = voucherEntity.getUsers();
        users.forEach(user -> user.getVouchers().remove(voucherEntity));
        voucherRepository.delete(voucherEntity);
        return "Delete voucher successfully";
    }

    @Override
    public VoucherDTO getDetail(Long id) {
        VoucherEntity voucherEntity = voucherRepository.findById(id).orElseThrow(() -> new NotFoundException("Voucher not found"));
        VoucherDTO responseVoucher = mapper.map(voucherEntity, VoucherDTO.class);
        responseVoucher.setShopId(voucherEntity.getShop().getId());
        responseVoucher.setImg(voucherEntity.getShop().getUserLoginData().getAvatarUrl());
        return responseVoucher;
    }

    @Override
    public PaginateDTO<VoucherDTO> getVouchers(int page, int perPage) {
        PaginateDTO<VoucherEntity> voucherEntityPaginateDTO = this.paginate(page, perPage);
        List<VoucherDTO> voucherDTOs = voucherEntityPaginateDTO.getPageData().stream()
                .map(voucherEntity -> {
                    VoucherDTO voucherDTO = mapper.map(voucherEntity, VoucherDTO.class);
                    voucherDTO.setImg(voucherEntity.getShop().getUserLoginData().getAvatarUrl());
                    voucherDTO.setShopId(voucherEntity.getShop().getId());
                    return voucherDTO;
                })
                .toList();
        Page<VoucherDTO> pageData = new PageImpl<>(voucherDTOs, PageRequest.of(page, perPage), perPage);
        return new PaginateDTO<>(pageData, voucherEntityPaginateDTO.getPagination());
    }

    @Override
    public VoucherDTO saveVoucherFromPost(Long userId, Long voucherId) {
        VoucherEntity voucherEntity = voucherRepository.findById(voucherId).orElseThrow(() -> new NotFoundException("Voucher not found"));
        UserLoginDataEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (!voucherEntity.getUsers().contains(userEntity) && voucherEntity.getEndTime() >= System.currentTimeMillis() && voucherEntity.getSaved() < voucherEntity.getQuantity()) {
            voucherEntity.getUsers().add(userEntity);
            voucherEntity = voucherRepository.save(voucherEntity);

            VoucherDTO voucherDTO = mapper.map(voucherEntity, VoucherDTO.class);
            voucherDTO.setImg(voucherEntity.getShop().getUserLoginData().getAvatarUrl());
            voucherDTO.setShopId(voucherEntity.getShop().getId());
            voucherDTO.setSaved(voucherEntity.getUsers().contains(userEntity) ? 1: 0);
            return voucherDTO;
        }
        throw new NotFoundException("Cant save voucher for user");
    }

    @Scheduled(cron = "0 0 0 * * *")
    void automaticallyDelete() {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            String spql = "DELETE FROM VoucherEntity v where v.endTime < FUNCTION('UNIX_TIMESTAMP') * 1000";
            entityManager.createQuery(spql).executeUpdate();
            transactionManager.commit(transaction);
        } catch (Exception e) {
            transactionManager.rollback(transaction);
            throw e;
        }
    }
}
