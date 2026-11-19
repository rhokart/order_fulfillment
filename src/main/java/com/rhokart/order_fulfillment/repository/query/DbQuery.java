package com.rhokart.order_fulfillment.repository.query;


public class DbQuery {

    public static final String SELECT_ADDRESS_BY_USER  = "SELECT addr_id, user_id, fname,lname, addr_line1, addr_line2, city, pincode,state, country, contact_no, landmark, is_active, addr_type FROM transaction.delivery_addr where user_id = #{userId} and is_active = true ;";

    public static final String INSERT_ORDER_INFO = " INSERT INTO transaction.order_info " +
            " (ord_id, cart_id, user_id, seller_id, order_at, sub_total, gst, delivery_charges, discount, offer, total_amt,total_units, coupon_cde, status, pay_status, pay_id, track_id, insert_by, insert_tmstp, update_by, update_tmstp, mstr_ord_id) " +
            " VALUES(#{orderId}, #{cartId}, #{userId}, #{sellerId}, CURRENT_TIMESTAMP, #{subTotal}, #{gst},#{deliveryCharges}, #{discount},#{offer},#{totalAmount},#{totalUnits}, #{couponCode}, #{status}, #{paymentStatus}, #{paymentId},#{trackingId}, #{insertBy}, CURRENT_TIMESTAMP, #{updateBy}, CURRENT_TIMESTAMP, #{masterOrderId});";

    public static final String INSERT_ORDER_DTL = "INSERT INTO transaction.order_detail (dtl_id, ord_id, sku_id, sku_nme, lst_id, lst_nme, quantity, total_amt, insert_by, insert_tmstp, update_by, update_tmstp) values (#{detailId}, #{orderId}, #{skuId}, #{skuName}, #{listingId}, #{listingName}, #{quantity},#{totalAmount}, #{insertBy}, CURRENT_TIMESTAMP, #{updateBy}, CURRENT_TIMESTAMP); ";

    public static final String INSERT_ORDER_TRACKING = "INSERT INTO transaction.order_tracking (trk_id, ord_id, addr_id, track_nbr, shipping_method, carrier, status, est_delivery_dte, delivered_at, attempt_count, is_signature, signature_by, exp_cde, insert_by, insert_tmstp, update_by, update_tmstp) VALUES(#{trackingId}, #{orderId}, #{addressId}, #{trackingNumber}, #{shippingMethod}, #{carrier}, #{status}, #{estimatedDeliveryDate},#{deliveredAt}, #{attemptCount}, #{isSignature},#{signatureBy}, #{exceptionCode}, #{insertBy}, CURRENT_TIMESTAMP,#{updateBy},CURRENT_TIMESTAMP); ";

    public static final String SELECT_ORDER_INFO = "SELECT ord_id, cart_id, user_id, seller_id, order_at, sub_total, gst, delivery_charges, discount, offer, total_amt, coupon_cde, status, pay_status, pay_id, track_id FROM transaction.order_info where ord_id = #{orderId}; ";

    public static final String INSERT_PAYMENT_INFO = "INSERT INTO transaction.payment_info (pay_id, amount, currency, status, pg_method, capture, contact, email, fee, tax, pg_dump) values (#{paymentId},#{amount}, #{currency}, #{status}, #{pgMethod}, #{isCapture}, #{contact}, #{email},#{fee}, #{tax}, #{pgDump});";

    public static final String ORDER_INFO_UPDATE = " update transaction.order_info set seller_invoice_id = #{invId} where ord_id = #{orderId}; ";

    public static final String ORDER_TRACKING_UPDATE = " update transaction.order_tracking  set otp = #{otp}, otp_tmstmp = #{otpTmstmp} where ord_id = #{orderId}; ";

    public static final String CART_INFO_UPDATE = " update usermgmt.cart_info set is_orderplace = true , ordered_tmstp = CURRENT_TIMESTAMP, update_tmstp = CURRENT_TIMESTAMP , update_by = #{updateBy} where cart_id = #{cartId} and user_id = #{userId}; ";

    public static final String SELECT_CART_DTL_USER = " SELECT cart_id, user_id, sku_id, sku_nme, listing_nme, listing_id, quantity, seller_id, seller_name, img_url,is_orderplace FROM usermgmt.cart_info where is_orderplace = false and cart_id = #{cartId} and user_id = #{userId}; ";

    public static final String SELECT_INVENTORY_SELLER = " SELECT sku_id, sku_nme, seller_id, seller_nme, units, gst, is_actv, unit_price_with_gst, unit_price_wo_gst, currency, unit_price_with_gst_with_commission, unit_price_wo_gst_with_commission FROM product.inventory_dtl where cart_id = #{cartId} and user_id = #{userId} and is_actv = true; ";

    public static final String UPDATE_INVENTORY_DETAIL = " update product.inventory_dtl set units = units - #{units} where sku_id = #{skuId} and seller_id = #{sellerId} and is_actv = true ;";

    public static final String UPDATE_DELIVERY_ADDRESS = "UPDATE transaction.delivery_addr SET fname=#{fname},lname=#{lname}, addr_line1= #{addressLine1}, addr_line2= #{addressLine2},addr_nickname = #{addrNickname}, city= #{city}, pincode=#{pincode}, state=#{state}, country= #{country}, contact_no= #{contactNo}, landmark= #{landmark}, addr_type= #{addressType} WHERE addr_id=#{addressId} and user_id = #{userId};";

    public static final String INSERT_DELIVERY_ADDRESS = " INSERT INTO transaction.delivery_addr (addr_id, user_id, fname, lname, addr_line1, addr_line2, addr_nickname, city, pincode,state, country, contact_no, landmark, is_active, addr_type) values (#{addressId}, #{userId}, #{fname},#{lname}, #{addressLine1}, #{addressLine2},#{addrNickname}, #{city}, #{pincode},#{state}, #{country}, #{contactNo}, #{landmark}, #{isActive}, #{addressType}); ";

    public static final String DELETE_DELIVERY_ADDRESS = " DELETE FROM transaction.delivery_addr WHERE addr_id = #{addressId} and user_id = #{userId} and is_active = true;";

    public static final String SELECT_ORDER_DTL = " SELECT dtl_id, ord_id, sku_id, sku_nme, lst_id, lst_nme, quantity,pckd_qty, total_amt,gst_pct,rhokart_pct,rhokart_comm_amount,rhokart_comm_with_gst, unit_price_wo_gst, unit_price_with_gst, insert_by, insert_tmstp, update_by, update_tmstp FROM transaction.order_detail where ord_id = #{orderId};";

    public static final String SELECT_INVENTORY_AVAILABILITY = " select units from product.inventory_dtl where sku_id = #{skuId} and seller_id = #{sellerId} and is_actv = true;";

    public static final String INSERT_ORDER_MSTR = "INSERT INTO transaction.order_mstr(mstr_ord_id, user_id, order_at, total_amt, status, pay_status, pay_id, insert_by, insert_tmstp, update_by, update_tmstp) values (#{masterOrderId}, #{userId}, CURRENT_TIMESTAMP, #{totalAmount}, #{status}, #{payStatus}, #{payId},#{insertBy}, CURRENT_TIMESTAMP, #{updateBy}, CURRENT_TIMESTAMP);";

    public static final String SELECT_ORDER_MSTR = "SELECT mstr_ord_id, user_id, order_at, total_amt, status, pay_status, pay_id, insert_by, insert_tmstp, update_by, update_tmstp FROM transaction.order_mstr where mstr_ord_id = #{masterOrderId} and user_id = #{userId} and status = #{status};";

    public static final String UPDATE_ORDER_MSTR = "UPDATE transaction.order_mstr SET order_at=CURRENT_TIMESTAMP, status=#{status}, pay_status=#{payStatus}, pay_id=#{payId}, update_tmstp= CURRENT_TIMESTAMP  WHERE mstr_ord_id=#{masterOrderId} AND user_id= #{userId};";

    public static final String SELECT_ORDER_TRACKING =
            "SELECT t.trk_id, t.ord_id, t.addr_id, t.track_nbr, t.shipping_method, t.carrier, t.status, " +
                    "t.est_delivery_dte, t.delivered_at, t.attempt_count, t.is_signature, t.signature_by, t.exp_cde, " +
                    "t.insert_by, t.insert_tmstp, t.update_by, t.update_tmstp, " +
                    "d.addr_id, d.user_id, d.addr_line1, d.addr_line2, d.city, d.pincode, d.country, d.contact_no, " +
                    "d.landmark, d.is_active, d.addr_type, d.fname, d.lname, d.state, t.otp " +
                    "FROM transaction.order_tracking t " +
                    "JOIN transaction.delivery_addr d ON t.addr_id = d.addr_id " +
                    "WHERE t.ord_id = #{orderId}";

    public static final String GET_CATG_FROM_LST_ID = "SELECT catg_id FROM product.lst_dtl WHERE lst_id = #{lstId}";

    public static final String GET_BRAND_TIER = "SELECT band_tier FROM product.band_tier_config WHERE catg_id = #{catgId} AND band_nme = #{brandName}";

    public static final String GET_COMMISION_DETAIL = "SELECT commission, partner_commission FROM product.commission_structure WHERE catg_id = #{catgId} AND band_tier = #{tier}";

    public static final String GET_ORDER_DETAIL_FROM_INVENTORY = "select inv.sku_id,inv.units,inv.gst_pct,inv.unit_price_with_gst,inv.unit_price_wo_gst,sku.hsn_cde from product.inventory_dtl inv join product.sku_dtl sku on inv.sku_id = sku.sku_id where inv.sku_id = #{skuId} and inv.seller_id = #{sellerId}";

    public static final String GET_DELIVERY_CHARGES = "SELECT transport_charges FROM transaction.transport_dtl WHERE ord_id = #{orderId};";
}
