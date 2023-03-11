package com.ndt2101.ezimarket.dto.GHN;

import com.ndt2101.ezimarket.model.OrderEntity;
import com.ndt2101.ezimarket.model.ProductEntity;
import com.ndt2101.ezimarket.model.ProductTypeEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateOrderPayload {
    private int payment_type_id = 2;
    private String note;
    private String from_name;
    private String from_phone;
    private String from_address;
    private String from_ward_name;
    private String from_district_name;
    private String from_province_name;
    private String to_name;
    private String to_phone;
    private String to_address;
    private String to_ward_name;
    private String to_district_name;
    private String to_province_name;
    private String return_phone;
    private String return_address;
    private String return_ward_name;
    private String return_district_name;
    private String return_province_name;
    private String client_order_code = "";
    private Integer cod_amount;
    private String content = "";
    private int weight;
    private int length;
    private int width;
    private int height;
    private int insurance_value;
    private int service_id = 53321;
    private int service_type_id = 3;
    private String required_note = "CHOXEMHANGKHONGTHU";
    private List<ProductItem> items;

    public static CreateOrderPayload createOrderPayload(OrderEntity orderEntity) {
        CreateOrderPayloadBuilder payload = CreateOrderPayload.builder();
        payload.payment_type_id = 2;
        payload.service_id = 53321;
        payload.service_type_id = 3;
        payload.note = orderEntity.getNoteToShop();
        payload.from_name = orderEntity.getShop().getName();
        payload.from_phone = orderEntity.getShop().getUserLoginData().getPhone();
        payload.from_address = orderEntity.getShop().getUserLoginData().getAddress().getDetailAddress();
        payload.from_ward_name = orderEntity.getShop().getUserLoginData().getAddress().getWard().getWardName();
        payload.from_district_name = orderEntity.getShop().getUserLoginData().getAddress().getDistrict().getDistrictName();
        payload.from_province_name = orderEntity.getShop().getUserLoginData().getAddress().getProvince().getProvinceName();
        payload.to_name = orderEntity.getShipTo().getUserLoginData().getFirstName() +  " " + orderEntity.getShipTo().getUserLoginData().getLastName();
        payload.to_phone = orderEntity.getShipTo().getUserLoginData().getPhone();
        payload.to_address = orderEntity.getShipTo().getUserLoginData().getAddress().getDetailAddress();
        payload.to_ward_name = orderEntity.getShipTo().getUserLoginData().getAddress().getWard().getWardName();
        payload.to_district_name = orderEntity.getShipTo().getUserLoginData().getAddress().getDistrict().getDistrictName();
        payload.to_province_name = orderEntity.getShipTo().getUserLoginData().getAddress().getProvince().getProvinceName();
        payload.return_phone =  orderEntity.getShop().getUserLoginData().getPhone();
        payload.return_address = payload.from_address;
        payload.return_ward_name = payload.from_ward_name;
        payload.return_district_name = payload.from_district_name;
        payload.return_province_name = payload.from_province_name;
        payload.cod_amount = Math.toIntExact(orderEntity.getTotalPrice());
        payload.insurance_value = Math.toIntExact(orderEntity.getTotalPrice());
        payload.required_note = "CHOXEMHANGKHONGTHU";

        List<ProductItem> productItems = new ArrayList<>();
        AtomicReference<Integer> totalWeight = new AtomicReference<>(0);
        orderEntity.getOrderItems().forEach(orderItemEntity -> {
            ProductItem.ProductItemBuilder productItem = ProductItem.builder();
            productItem.name(orderItemEntity.getProductType().getType());
            productItem.code(String.valueOf(orderItemEntity.getProductType().getId()));
            productItem.quantity(Math.toIntExact(orderItemEntity.getItemQuantity()));
            productItem.price(Math.toIntExact(orderItemEntity.getProductType().getPrice()));
            productItem.weight(Math.round(orderItemEntity.getProductType().getProduct().getWeight()));
            productItems.add(productItem.build());

            totalWeight.updateAndGet(v -> Math.round(v + orderItemEntity.getProductType().getProduct().getWeight() * orderItemEntity.getItemQuantity()));
        });
        payload.items = productItems;
        payload.weight = totalWeight.get();

        AtomicReference<Integer> totalHeight = new AtomicReference<>(0);
        AtomicReference<Integer> totalLength = new AtomicReference<>(0);
        AtomicReference<Integer> totalWidth = new AtomicReference<>(0);
        Map<Long, ProductTypeEntity> productTypeEntityMap = new HashMap<>();
        orderEntity.getOrderItems().forEach(orderItemEntity -> {
            ProductTypeEntity productTypeEntity = productTypeEntityMap.get(orderItemEntity.getProductType().getId());
            if (productTypeEntity == null) {
                productTypeEntity = orderItemEntity.getProductType();
                productTypeEntity.setQuantity(orderItemEntity.getItemQuantity());
            } else {
                Long quantity = productTypeEntity.getQuantity() + orderItemEntity.getItemQuantity();
                productTypeEntity.setQuantity(quantity);
            }
            productTypeEntityMap.put(
                    orderItemEntity.getProductType().getId(),
                    productTypeEntity
            );
        });

        Map<Long, ProductEntity> productEntityMap = new HashMap<>();
        productTypeEntityMap.values().forEach(productTypeEntity -> {
            ProductEntity productEntity = productEntityMap.get(productTypeEntity.getProduct().getId());
            if (productEntity == null) {
                productEntity = productTypeEntity.getProduct();
                productEntity.setProductTypes(new ArrayList<>());
            }

            productEntity.getProductTypes().add(productTypeEntity);

            productEntityMap.put(
                    productTypeEntity.getProduct().getId(),
                    productEntity
            );
        });

        productEntityMap.values().forEach(productEntity -> {
            totalHeight.updateAndGet(h -> Math.round(h + productEntity.getHeight()));
            totalLength.updateAndGet(h -> Math.round(h + productEntity.getLength()));
            totalWidth.updateAndGet(h -> Math.round(h + productEntity.getLength() * productEntity.getProductTypes().size()));
        });
        payload.length = totalLength.get();
        payload.height = totalHeight.get();
        payload.width = totalWidth.get();

        return payload.build();
    }

}
