package com.rhokart.order_fulfillment.service.helper;

import com.rhokart.order_fulfillment.dto.CommDataResponse;
import com.rhokart.order_fulfillment.dto.request.EmailDetails;
import com.rhokart.order_fulfillment.dto.request.FileCreatorRequest;
import com.rhokart.order_fulfillment.repository.ListingRepo;
import com.rhokart.order_fulfillment.repository.SellerRepository;
import com.rhokart.order_fulfillment.repository.TransactionRepo;
import com.rhokart.order_fulfillment.repository.UserRepository;
import com.rhokart.order_fulfillment.repository.model.*;
import com.rhokart.order_fulfillment.util.GlobalValidator;
import com.rhokart.order_fulfillment.util.HtmlLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class SellerFileCreator {
    private final S3Client amazonS3Client;
    private final HtmlLoader htmlLoader;
    private final EmailService emailService;
    @Value("${application.aws.s3.bucket.name}")
    String bucketName;
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ListingRepo listingRepo;

    public SellerFileCreator(S3Client amazonS3Client, HtmlLoader htmlLoader, EmailService emailService) {
        this.amazonS3Client = amazonS3Client;
        this.htmlLoader = htmlLoader;
        this.emailService = emailService;
    }

    public void createPdf(FileCreatorRequest request) throws Exception {
        String fileName = "RHOSEL".concat(request.getOrderId()).concat(".pdf");
        String htmlContent = htmlLoader.loadSellerHtmlTemplate();
        OrderInfo orderInfo = transactionRepo.getOrderInfo(request.getOrderId());
        List<OrderDetail> orderDetails = transactionRepo.getOrderDetailsFromDb(request.getOrderId());
        SellerDocument sellerDetail = sellerRepository.findSellerDetailsById(request.getSellerId());
//        OrderTrackingDTO orderTrackingDetail = transactionRepo.getOrderTrackingByOrderId(request.getOrderId());
        UserDetailsDTO userDetailsDTO = userRepository.findUserDetailsById(orderInfo.getUserId());

        double amountPaid = orderInfo.getTotalAmount() / 100.0;
        double dueAmount = 0;
        String gstType = Optional.of(sellerDetail.getSellerAddress().getPickupAddress().getState().trim()).orElse("").equalsIgnoreCase("Karnataka") ? "SGST" : "IGST";
        htmlContent = htmlContent
                .replace("${orderNum}", request.getOrderId())
                .replace("${DateTime}", "")
                .replace("${invNum}", "RHOSEL".concat(request.getOrderId()))
                .replace("${invDateTime}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .replace("${sellerTradeDisplayName}", Optional.ofNullable(sellerDetail.getBusinessDetail().getTradeName()).orElse(""))
                .replace("${sellerBusinessName}", Optional.ofNullable(sellerDetail.getBusinessDetail().getEntityName()).orElse(""))
                .replace("${sellerAddress1}", Optional.ofNullable(sellerDetail.getSellerAddress().getPickupAddress().getAddressLine1()).orElse(""))
                .replace("${sellerAddress2}", Optional.ofNullable(sellerDetail.getSellerAddress().getPickupAddress().getAddressLine2()).orElse(""))
                .replace("${sellerDistrict}", Optional.ofNullable(sellerDetail.getSellerAddress().getPickupAddress().getDistrict()).orElse(""))
                .replace("${sellerState}", Optional.ofNullable(sellerDetail.getSellerAddress().getPickupAddress().getState()).orElse(""))
                .replace("${sellerPincode}", Optional.ofNullable(sellerDetail.getSellerAddress().getPickupAddress().getPincode()).orElse(""))
                .replace("${sellerCountry}", Optional.ofNullable(sellerDetail.getSellerAddress().getPickupAddress().getCountry()).orElse(""))
                .replace("${sellerPAN}", Optional.ofNullable(sellerDetail.getBusinessDetail().getPan()).orElse(""))
                .replace("${sellerGST}", Optional.ofNullable(sellerDetail.getBusinessDetail().getGstin()).orElse(""));

        StringBuilder tableRows = new StringBuilder();
        int counter = 1;
        int totalQty = 0;
        int totalUnitPriceWoGst = 0;
        int totalUnitPriceWithGst = 0;

        for (OrderDetail order : orderDetails) {
            totalQty += order.getPckdQty();
            int unitPriceWoGst = order.getUnitPriceWoGst() - order.getRhokartComm();
            int unitPriceWithGst = order.getUnitPriceWithGst() - order.getRhokartCommWithGst();
            totalUnitPriceWoGst += order.getUnitPriceWoGst() - order.getRhokartComm();
            totalUnitPriceWithGst += order.getUnitPriceWithGst() - (order.getRhokartCommWithGst());
            Optional<OrderDetailInventory> detailInventory = transactionRepo.getOrderDetailInventory(order.getSkuId(), orderInfo.getSellerId());
            tableRows.append("<tr>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(counter++).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(order.getListingName().replace("&","and").concat(",").concat(order.getSkuName().replace("&","and")).concat(" - ").concat(order.getSkuId())).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(detailInventory.get().getHsnCode()).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(formatIndianAmount(BigDecimal.valueOf(unitPriceWoGst).divide(BigDecimal.valueOf(100)).doubleValue())).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(String.valueOf(order.getGstPct()).concat("% ").concat(gstType)).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(order.getPckdQty()).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(formatIndianAmount(BigDecimal.valueOf((long) order.getPckdQty() * unitPriceWoGst).divide(BigDecimal.valueOf(100)).doubleValue())).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(formatIndianAmount(BigDecimal.valueOf((long) order.getPckdQty() * (unitPriceWithGst - unitPriceWoGst)).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP).doubleValue())).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(formatIndianAmount(BigDecimal.valueOf((long) order.getPckdQty() * unitPriceWithGst).divide(BigDecimal.valueOf(100)).doubleValue())).append("</td>")
                    .append("</tr>");
        }

//
//        for (Map.Entry<Double, Double> entry : commByPct.entrySet()) {
//            tableRows.append("<tr>")
//                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
//                    .append(counter++).append("</td>")
//                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
//                    .append("Rhokart Commission").append("</td>")
//                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
//                    .append("-").append("</td>")
//                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
//                    .append(String.valueOf(entry.getKey()).concat("% ").concat(gstType)).append("</td>")
//                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
//                    .append(formatIndianAmount(entry.getValue())).append("</td>")
//                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
//                    .append(formatIndianAmount(BigDecimal.valueOf(entry.getValue()).multiply(BigDecimal.valueOf(0.18)).doubleValue())).append("</td>")
//                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
//                    .append(formatIndianAmount(BigDecimal.valueOf(entry.getValue()).multiply(BigDecimal.valueOf(1.18)).doubleValue())).append("</td>")
//                    .append("</tr>");
//        }


        if (sellerDetail != null && sellerDetail.getDocuments() != null) {
            // Use stream to find the document with fileName "signature" and extract the fileUrl
            Optional<String> signatureFileUrl = sellerDetail.getDocuments().stream()
                    .filter(doc -> "signature".equals(doc.getFileName()))
                    .map(Documents::getFileUrl)
                    .findFirst();

            htmlContent = htmlContent
                    .replace("${signatureImagePath}", signatureFileUrl.orElse(""));

        }

        //todo: Determine GST type

        // Determine logistics, platform, and discount

        log.info("html is done.");
        double deliveryCharges = transactionRepo.getDeliveryChargesFromDb(request.getOrderId()).doubleValue();

        BigDecimal logBaseAmount = BigDecimal.valueOf(deliveryCharges)
                .divide(BigDecimal.valueOf(1.18), 2, RoundingMode.HALF_UP);

        BigDecimal logGstAmount = BigDecimal.valueOf(deliveryCharges)
                .subtract(logBaseAmount)
                .setScale(2, RoundingMode.HALF_UP);

        double totalPriceWoGst = BigDecimal.valueOf(totalUnitPriceWoGst)
                .divide(BigDecimal.valueOf(100)).doubleValue();

        double totalPriceWithGst = BigDecimal.valueOf(totalUnitPriceWithGst)
                .divide(BigDecimal.valueOf(100)).doubleValue();

        htmlContent = htmlContent
                .replace("${tableContent}", tableRows)
                .replace("${headCount1}", String.valueOf(counter++))
                .replace("${headCount2}", String.valueOf(counter++))
                .replace("${headCount3}", String.valueOf(counter))
                .replace("${Platwogst}", "0.0")
                .replace("${totalPlatwogst}", "0.0")
                .replace("${totalPlatgst}", "0.0")
                .replace("${totalPlatgwithgst}", "0.0")
                .replace("${logwogst}", String.valueOf(logBaseAmount))
                .replace("${totallogwogst}", String.valueOf(logBaseAmount))
                .replace("${totallogwithgst}", String.valueOf(deliveryCharges))
                .replace("${totalloggst}", String.valueOf(logGstAmount))
                .replace("${discwogst}", String.valueOf(orderInfo.getDiscount() / 100.0))
                .replace("${totaldiscwithgst}", String.valueOf(orderInfo.getDiscount() / 100.0 + ((orderInfo.getDiscount() / 100.0) * 0.18)))
                .replace("${totaldiscwogst}", String.valueOf((orderInfo.getDiscount() / 100.0)))
                .replace("${gstType}", gstType)
                .replace("${totalQty}", String.valueOf(totalQty))
                .replace("${PaidInvoiceValue}", String.valueOf(amountPaid))
                .replace("${DueInvoiceValue}", String.valueOf(dueAmount))
                .replace("${OrdDateTime}", String.valueOf(orderInfo.getOrderAt().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"))))
                .replace("${netTotal}", String.valueOf(formatIndianAmount(totalPriceWoGst + logBaseAmount.doubleValue() + (orderInfo.getDiscount() / 100.0))))
                .replace("${totalInvoiceValue}", String.valueOf(formatIndianAmount(totalPriceWithGst + deliveryCharges + (orderInfo.getDiscount() / 100.0))))
                .replace("${totalGstAmount}", String.valueOf(formatIndianAmount(totalPriceWithGst - totalPriceWoGst + logGstAmount.doubleValue())))
                .replace("${amountInWords}", GlobalValidator.convertRupeesToWords(totalPriceWithGst + deliveryCharges + (orderInfo.getDiscount() / 100.0)));

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(byteArrayOutputStream, true);

            byte[] contentBytes = byteArrayOutputStream.toByteArray();
            Long contentLength = Long.valueOf(contentBytes.length);

            log.info("all preparations is done. - s3 started");
            // Create the PDF file in the specified bucket
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentLength(contentLength)
                    .build();

            PutObjectResponse result = amazonS3Client.putObject(putObjectRequest, RequestBody.fromBytes(contentBytes));

            log.info("PDF file uploaded to S3: {}", result.eTag());
            // Write the PDF file locally
//            try (FileOutputStream fileOutputStream = new FileOutputStream(new File(fileName))) {
//                fileOutputStream.write(contentBytes);
//            }

            // capture and send OTP to user
            captureAndSendOtp(request, userDetailsDTO.getEmail());

            log.info("Pdf created -> ".concat(fileName));
        } catch (Exception e) {
            log.error("Error creating PDF: {}", e.getMessage(), e);
        }
    }

    public CommDataResponse doCommData(String lstId) {
        // Step 1: Get catg_id from lst_dtl
        Integer catgId = transactionRepo.getCatgIdByLstId(lstId.trim());

        // Step 2: Get brandName from MongoDB
        ListingDetail listingDetail = listingRepo.findById(lstId.trim())
                .orElseThrow(() -> new RuntimeException("Listing not found in Mongo"));
        String brandName = listingDetail.getBrandName();

        // Step 3: Get tier from brand_tier
        Integer tier = transactionRepo.getTierByCatgIdAndBrandName(catgId, brandName);

        // Step 4: Get comm from comm_struct
        CommDetail comm = transactionRepo.getCommByCatgIdAndTier(catgId, tier);

        return CommDataResponse.builder()
                .catgId(catgId)
                .brandName(brandName)
                .tier(tier)
                .comm(comm)
                .build();
    }

    public int getSellerPrice(int totalAmountPaise, BigDecimal commissionPercent) {
        // Convert commission percent to decimal (e.g., 18 → 0.18)
        BigDecimal commissionRate = commissionPercent.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

        // MRP = total / (1 + commission rate)
        BigDecimal total = BigDecimal.valueOf(totalAmountPaise);
        BigDecimal divisor = BigDecimal.ONE.add(commissionRate);
        BigDecimal mrp = total.divide(divisor, 0, RoundingMode.HALF_DOWN); // Result in paisa

        return mrp.intValue(); // Return MRP in paisa
    }

    public String formatIndianAmount(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        DecimalFormat formatter = new DecimalFormat("##,##,##0.00", symbols);
        return formatter.format(amount);
    }

    public void captureAndSendOtp(FileCreatorRequest request, String email){
        String otp = GlobalValidator.generateOtp();
        log.info("otp flow");
        transactionRepo.updateOrderTrackingWithOtp(otp, request.getOrderId() ,LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")));
        log.info("otp updated");
        emailService.sendEmailToUser(EmailDetails.builder()
                .msgBody("Delivery OTP for Order ID: " + request.getOrderId() + " is " + otp +
                        "\n\n" +
                        "\n Regards\nRhoselect Private limited")
                .toAddress(email)
                .subject("RHOKART Delivery OTP").build());
    }
}
