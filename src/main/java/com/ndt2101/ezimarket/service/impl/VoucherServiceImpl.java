package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.dto.VoucherDTO;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.ShopEntity;
import com.ndt2101.ezimarket.model.UserLoginDataEntity;
import com.ndt2101.ezimarket.model.VoucherEntity;
import com.ndt2101.ezimarket.repository.ShopRepository;
import com.ndt2101.ezimarket.repository.VoucherRepository;
import com.ndt2101.ezimarket.service.VoucherService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class VoucherServiceImpl implements VoucherService {
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
