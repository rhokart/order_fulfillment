package com.rhokart.order_fulfillment.repository;

import com.rhokart.order_fulfillment.repository.model.*;
import com.rhokart.order_fulfillment.repository.query.DbQuery;
import com.rhokart.order_fulfillment.util.OrderDbMapper;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@OrderDbMapper
public interface TransactionRepo {

    @Select(DbQuery.SELECT_ADDRESS_BY_USER)
    @Results({
            @Result(property = "addressId", column = "addr_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "addressLine1", column = "addr_line1"),
            @Result(property = "addressLine2", column = "addr_line2"),
            @Result(property = "addrNickname", column = "addr_nickname"),
            @Result(property = "city", column = "city"),
            @Result(property = "pincode", column = "pincode"),
            @Result(property = "country", column = "country"),
            @Result(property = "contactNo", column = "contact_no"),
            @Result(property = "landmark", column = "landmark"),
            @Result(property = "isActive", column = "is_active"),
            @Result(property = "addressType", column = "addr_type"),
            @Result(property = "lname", column = "lname"),
            @Result(property = "fname", column = "fname"),
            @Result(property = "state", column = "state")
    })
    List<DeliveryAddress> fetchDeliveryAddressByUserId(@Param("userId") String userId);

    @Update(DbQuery.UPDATE_DELIVERY_ADDRESS)
    void updateDeliveryAddressForUser(@Param("fname") String fname, @Param("lname") String lname, @Param("addressLine1") String addressLine1, @Param("addressLine2") String addressLine2,@Param("addrNickname") String addrNickname, @Param("city") String city,
                                      @Param("pincode") String pincode, @Param("state") String state, @Param("country") String country, @Param("contactNo") String contactNo, @Param("landmark") String landmark,
                                      @Param("addressType") String addressType, @Param("addressId") String addressId, @Param("userId") String userId);


    @Insert(DbQuery.INSERT_DELIVERY_ADDRESS)
    void insertDeliveryAddressForUser(DeliveryAddress address);

    @Delete(DbQuery.DELETE_DELIVERY_ADDRESS)
    boolean deleteDeliveryAddressByIdAndUserId(@Param("addressId") String addressId, @Param("userId") String userId);

    @Insert(DbQuery.INSERT_ORDER_INFO)
    void insertDataIntoOrderInfo(OrderInfo orderInfo);

    @Insert(DbQuery.INSERT_ORDER_MSTR)
    void insertDataIntoOrderMaster(OrderMaster orderMaster);

    @Insert((DbQuery.INSERT_ORDER_DTL))
    void insertDataIntoOrderDetail(OrderDetail orderDetail);

    @Insert(DbQuery.INSERT_ORDER_TRACKING)
    void insertDataIntoOrderTracking(OrderTracking orderTracking);

    @Select(DbQuery.SELECT_ORDER_INFO)
    @Results({
            @Result(property = "orderId", column = "ord_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "sellerId", column = "seller_id"),
            @Result(property = "orderAt", column = "order_at"),
            @Result(property = "subTotal", column = "sub_total"),
            @Result(property = "gst", column = "gst"),
            @Result(property = "deliveryCharges", column = "delivery_charges"),
            @Result(property = "discount", column = "discount"),
            @Result(property = "offer", column = "offer"),
            @Result(property = "totalAmount", column = "total_amt"),
            @Result(property = "paymentId", column = "pay_id")
    })
    OrderInfo getOrderInfo(@Param("orderId") String orderId);

    @Select(DbQuery.SELECT_ORDER_MSTR)
    @Results({
            @Result(property = "masterOrderId", column = "mstr_ord_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "payStatus", column = "pay_status"),
            @Result(property = "totalAmount", column = "total_amt"),

    })
    OrderMaster selectOrderMasterCountForUser(@Param("masterOrderId") String masterOrderId,
                                              @Param("userId") String userId,
                                              @Param("status") String status);

    @Update(DbQuery.UPDATE_ORDER_MSTR)
    void updateOrderMasterWithOrderStatus(@Param("masterOrderId") String masterOrderId,
                                          @Param("userId") String userId,
                                          @Param("status") String status,
                                          @Param("payStatus") String payStatus,
                                          @Param("payId") String payId);

    @Insert(DbQuery.INSERT_PAYMENT_INFO)
    void insertDataIntoPaymentInfo(PaymentDetail paymentDetail);

    @Update(DbQuery.ORDER_INFO_UPDATE)
    void updateOrderInfoWithSellerInvId(@Param("orderId") String orderId, @Param("invId") String invId);

    @Update(DbQuery.ORDER_TRACKING_UPDATE)
    void updateOrderTrackingWithOtp(@Param("otp") String otp,
                                            @Param("orderId") String orderId,
                                            @Param("otpTmstmp") String otpTmstmp);

    @Update(DbQuery.CART_INFO_UPDATE)
    void updateCartInfowithOrder(@Param("updateBy") String updateBy,
                                 @Param("cartId") Integer cartId,
                                 @Param("userId") String userId);

    @Select(DbQuery.SELECT_CART_DTL_USER)
    @Results({
            @Result(property = "cartId", column = "cart_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "skuId", column = "sku_id"),
            @Result(property = "skuName", column = "sku_nme"),
            @Result(property = "listingName", column = "listing_nme"),
            @Result(property = "listingId", column = "listing_id"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "sellerId", column = "seller_id"),
            @Result(property = "sellerName", column = "seller_name"),
            @Result(property = "imgUrl", column = "img_url"),
            @Result(property = "isOrderPlace", column = "is_orderplace")
    })
    List<CartInfo> getCartDetailFromDb(@Param("cartId") Integer cartId, @Param("userId") String userId);


    @Select(DbQuery.SELECT_INVENTORY_SELLER)
    @Results({
            @Result(column = "sku_id", property = "skuId"),
            @Result(column = "sku_nme", property = "skuName"),
            @Result(column = "seller_id", property = "sellerId"),
            @Result(column = "seller_nme", property = "sellerName"),
            @Result(column = "units", property = "units"),
            @Result(column = "gst", property = "gst"),
            @Result(column = "is_actv", property = "isActive"),
            @Result(column = "unit_price_with_gst", property = "unitPriceWithGst"),
            @Result(column = "unit_price_wo_gst", property = "unitPriceWithoutGst"),
            @Result(column = "currency", property = "currency"),
            @Result(column = "unit_price_with_gst_with_commission", property = "unitPriceWithGstWithCommission"),
            @Result(column = "unit_price_wo_gst_with_commission", property = "unitPriceWithoutGstWithCommission")
    })
    List<InventoryDetails> getInventoryDetailsByUser(@Param("sellerId") String sellerId, @Param("userId") String userId);


    @Select(DbQuery.SELECT_ORDER_DTL)
    @Results({
            @Result(property = "detailId", column = "dtl_id"),
            @Result(property = "orderId", column = "ord_id"),
            @Result(property = "skuId", column = "sku_id"),
            @Result(property = "skuName", column = "sku_nme"),
            @Result(property = "listingId", column = "lst_id"),
            @Result(property = "listingName", column = "lst_nme"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "pckdQty", column = "pckd_qty"),
            @Result(property = "totalAmount", column = "total_amt"),
            @Result(property = "gstPct", column = "gst_pct"),
            @Result(property = "rhokartPct", column = "rhokart_pct"),
            @Result(property = "rhokartComm", column = "rhokart_comm_amount"),
            @Result(property = "rhokartCommWithGst", column = "rhokart_comm_with_gst"),
            @Result(property = "unitPriceWithGst", column = "unit_price_with_gst"),
            @Result(property = "unitPriceWoGst", column = "unit_price_wo_gst")
    })
    List<OrderDetail> getOrderDetailsFromDb(@Param("orderId") String orderId);

    @Update(DbQuery.UPDATE_INVENTORY_DETAIL)
    void updateInventoryDetailWithItems(@Param("units") Integer units, @Param("skuId") String skuId, @Param("sellerId") String sellerId);

    @Select(DbQuery.SELECT_INVENTORY_AVAILABILITY)
    Integer getInventoryAvailabilityBySeller(@Param("skuId") String skuId, @Param("sellerId") String sellerId);

    @Select(DbQuery.SELECT_ORDER_TRACKING)
    @Results({
            @Result(property = "trkId", column = "trk_id"),
            @Result(property = "ordId", column = "ord_id"),
            @Result(property = "addrId", column = "addr_id"),
            @Result(property = "trackNbr", column = "track_nbr"),
            @Result(property = "shippingMethod", column = "shipping_method"),
            @Result(property = "carrier", column = "carrier"),
            @Result(property = "status", column = "status"),
            @Result(property = "estDeliveryDte", column = "est_delivery_dte"),
            @Result(property = "deliveredAt", column = "delivered_at"),
            @Result(property = "attemptCount", column = "attempt_count"),
            @Result(property = "isSignature", column = "is_signature"),
            @Result(property = "signatureBy", column = "signature_by"),
            @Result(property = "expCde", column = "exp_cde"),
            @Result(property = "insertBy", column = "insert_by"),
            @Result(property = "insertTmstp", column = "insert_tmstp"),
            @Result(property = "updateBy", column = "update_by"),
            @Result(property = "updateTmstp", column = "update_tmstp"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "addrLine1", column = "addr_line1"),
            @Result(property = "addrLine2", column = "addr_line2"),
            @Result(property = "city", column = "city"),
            @Result(property = "pincode", column = "pincode"),
            @Result(property = "country", column = "country"),
            @Result(property = "contactNo", column = "contact_no"),
            @Result(property = "landmark", column = "landmark"),
            @Result(property = "isActive", column = "is_active"),
            @Result(property = "addrType", column = "addr_type"),
            @Result(property = "fname", column = "fname"),
            @Result(property = "lname", column = "lname"),
            @Result(property = "state", column = "state"),
            @Result(property = "otp", column = "otp")
    })
    OrderTrackingDTO getOrderTrackingByOrderId(@Param("orderId") String orderId);

    @Select(DbQuery.GET_CATG_FROM_LST_ID)
    Integer getCatgIdByLstId(@Param("lstId") String lstId);

    @Select(DbQuery.GET_BRAND_TIER)
    Integer getTierByCatgIdAndBrandName(@Param("catgId") Integer catgId, @Param("brandName") String brandName);

    @Select(DbQuery.GET_COMMISION_DETAIL)
    @Results({
            @Result(property = "rhokartComm", column = "commission"),
            @Result(property = "partnerComm", column = "partner_commission")
    })
    CommDetail getCommByCatgIdAndTier(@Param("catgId") Integer catgId, @Param("tier") Integer tier);

    @Select(DbQuery.GET_ORDER_DETAIL_FROM_INVENTORY)
    @Results({
            @Result(column = "sku_id", property = "skuId"),
            @Result(column = "units", property = "units"),
            @Result(column = "gst_pct", property = "gst"),
            @Result(column = "unit_price_with_gst", property = "unitPriceWithGst"),
            @Result(column = "unit_price_wo_gst", property = "unitPriceWithoutGst"),
            @Result(column = "hsn_cde", property = "hsnCode"),
    })
    Optional<OrderDetailInventory> getOrderDetailInventory(@Param("skuId") String skuId, @Param("sellerId") String sellerId);

    @Select(DbQuery.GET_DELIVERY_CHARGES)
    BigDecimal getDeliveryChargesFromDb(@Param("orderId") String orderId);
}
