package com.rhokart.order_fulfillment.service.helper;

import com.rhokart.order_fulfillment.dto.CommDataResponse;
import com.rhokart.order_fulfillment.dto.request.EmailDetails;
import com.rhokart.order_fulfillment.dto.request.FileCreatorRequest;
import com.rhokart.order_fulfillment.dto.request.UserOrder;
import com.rhokart.order_fulfillment.repository.ListingRepo;
import com.rhokart.order_fulfillment.repository.SellerRepository;
import com.rhokart.order_fulfillment.repository.TransactionRepo;
import com.rhokart.order_fulfillment.repository.UserRepository;
import com.rhokart.order_fulfillment.repository.model.*;
import com.rhokart.order_fulfillment.util.GlobalValidator;
import com.rhokart.order_fulfillment.util.HtmlLoader;
import com.rhokart.order_fulfillment.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
public class FileCreator {
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

    public FileCreator(S3Client amazonS3Client, HtmlLoader htmlLoader, EmailService emailService) {
        this.amazonS3Client = amazonS3Client;
        this.htmlLoader = htmlLoader;
        this.emailService = emailService;
    }

    public String createPdf(FileCreatorRequest request) throws Exception {
        String fileName = "RHOBUY".concat(request.getOrderId()).concat(".pdf");
        String htmlContent = htmlLoader.loadHtmlTemplate();
        OrderInfo orderInfo = transactionRepo.getOrderInfo(request.getOrderId());
        List<OrderDetail> orderDetails = transactionRepo.getOrderDetailsFromDb(request.getOrderId());
        SellerDocument sellerDetail = sellerRepository.findSellerDetailsById(request.getSellerId());
        OrderTrackingDTO orderTrackingDetail = transactionRepo.getOrderTrackingByOrderId(request.getOrderId());
        UserDetailsDTO userDetailsDTO = userRepository.findUserDetailsById(orderInfo.getUserId());
        double amountPaid = orderInfo.getTotalAmount() / 100.0;
        double dueAmount = 0;
        String gstType = Optional.of(sellerDetail.getSellerAddress().getPickupAddress().getState().trim()).orElse("").equalsIgnoreCase(orderTrackingDetail.getState().trim()) ? "SGST" : "IGST";
        htmlContent = htmlContent
                .replace("${orderNum}", request.getOrderId())
                .replace("${DateTime}", "")
                .replace("${invNum}", "RHOBUY".concat(request.getOrderId()))
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
                .replace("${sellerGST}", Optional.ofNullable(sellerDetail.getBusinessDetail().getGstin()).orElse(""))
//                .replace("${buyerBusinessDetails}", Optional.ofNullable(userDetailsDTO.getBusinessDetail().getBusinessName()).orElse(""))
                .replace("${buyerAddress}", Optional.ofNullable(orderTrackingDetail.getAddrLine1()).orElse("").concat(Optional.ofNullable(orderTrackingDetail.getAddrLine2()).orElse("")))
                .replace("${buyerDistrict}", Optional.ofNullable(orderTrackingDetail.getCity()).orElse(""))
                .replace("${buyerState}", Optional.ofNullable(orderTrackingDetail.getState()).orElse(""))
                .replace("${buyerPincode}", Optional.ofNullable(orderTrackingDetail.getPincode()).orElse(""))
                .replace("${buyerCountry}", "India")
                .replace("${buyerGST}", userDetailsDTO.isProfileCompleted() ? Optional.ofNullable(userDetailsDTO.getBusinessDetail().getGstNumber()).orElse("N/A") : "N/A")
                .replace("${shippingName}", Optional.ofNullable(orderTrackingDetail.getFname()).orElse("").concat(" ").concat(Optional.ofNullable(orderTrackingDetail.getLname()).orElse("")))
                .replace("${shippingAddress1}", Optional.ofNullable(orderTrackingDetail.getAddrLine1()).orElse(""))
                .replace("${shippingAddress2}", Optional.ofNullable(orderTrackingDetail.getAddrLine2()).orElse(""))
                .replace("${shippingDistrict}", Optional.ofNullable(orderTrackingDetail.getCity()).orElse(""))
                .replace("${shippingState}", Optional.ofNullable(orderTrackingDetail.getState()).orElse(""))
                .replace("${shippingPincode}", Optional.ofNullable(orderTrackingDetail.getPincode()).orElse(""))
                .replace("${shippingCountry}", Optional.ofNullable(orderTrackingDetail.getCountry()).orElse(""))
                .replace("${contactName}", Optional.ofNullable(orderTrackingDetail.getFname()).orElse("").concat(" ").concat(Optional.ofNullable(orderTrackingDetail.getLname()).orElse("")))
                .replace("${contactPhone}", Optional.ofNullable(orderTrackingDetail.getContactNo()).orElse(""));

        StringBuilder tableRows = new StringBuilder();
        int counter = 1;
        int totalQty = 0;
        double totalAmountWoGst = 0.0;
        double totalGst = 0.0;
        double totalAmountWithGst = 0.0;

        for (OrderDetail order : orderDetails) {
            totalQty += order.getPckdQty();

            Optional<OrderDetailInventory> detailInventory = transactionRepo.getOrderDetailInventory(order.getSkuId(), orderInfo.getSellerId());
            int totalUnitPriceWithGst = order.getUnitPriceWithGst();
            int totalUnitPriceWoGst = order.getUnitPriceWoGst();

            totalAmountWoGst += BigDecimal.valueOf((long) order.getPckdQty() * totalUnitPriceWoGst).divide(BigDecimal.valueOf(100)).doubleValue();
            totalGst += BigDecimal.valueOf((long) order.getPckdQty() * (totalUnitPriceWithGst - totalUnitPriceWoGst)).divide(BigDecimal.valueOf(100)).doubleValue();
            totalAmountWithGst += BigDecimal.valueOf((long) order.getPckdQty() * totalUnitPriceWithGst).divide(BigDecimal.valueOf(100)).doubleValue();
            tableRows.append("<tr>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(counter++).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(order.getListingName().replace("&","and").concat(",").concat(order.getSkuName().replace("&","and")).concat(" - ").concat(order.getSkuId())).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(detailInventory.get().getHsnCode()).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(formatIndianAmount(BigDecimal.valueOf(totalUnitPriceWoGst).divide(BigDecimal.valueOf(100)).doubleValue())).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(String.valueOf(order.getGstPct()).concat("% ").concat(gstType)).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(order.getPckdQty()).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(formatIndianAmount(BigDecimal.valueOf((long) order.getPckdQty() * totalUnitPriceWoGst).divide(BigDecimal.valueOf(100)).doubleValue())).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(formatIndianAmount(BigDecimal.valueOf((long) order.getPckdQty() * (totalUnitPriceWithGst - totalUnitPriceWoGst)).divide(BigDecimal.valueOf(100)).doubleValue())).append("</td>")
                    .append("<td style=\"border: 1px solid #ddd; padding: 4px; text-align: left; word-wrap: break-word; white-space: normal;\">")
                    .append(formatIndianAmount(BigDecimal.valueOf((long) order.getPckdQty() * totalUnitPriceWithGst).divide(BigDecimal.valueOf(100)).doubleValue())).append("</td>")
                    .append("</tr>");
        }

        String jwt = JWTUtil.generateToken(request.getOrderId());
        log.info("Generated JWT: {}", jwt);
        String qrData = "rhokart://verify?token=" + jwt;
        BufferedImage image = GlobalValidator.generateQRCode(qrData);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);

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
        double deliveryCharges = orderInfo.getDeliveryCharges() / 100.0;

        BigDecimal logBaseAmount = BigDecimal.valueOf(deliveryCharges)
                .divide(BigDecimal.valueOf(1.18), 2, RoundingMode.HALF_UP);

        BigDecimal logGstAmount = BigDecimal.valueOf(deliveryCharges)
                .subtract(logBaseAmount)
                .setScale(2, RoundingMode.HALF_UP);

        htmlContent = htmlContent
                .replace("${tableContent}", tableRows)
                .replace("${headCount1}", String.valueOf(counter++))
                .replace("${headCount2}", String.valueOf(counter++))
                .replace("${headCount3}", String.valueOf(counter))
                .replace("${totalQty}", String.valueOf(totalQty))
                .replace("${gstType}", gstType)
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
                .replace("${PaidInvoiceValue}", String.valueOf(amountPaid))
                .replace("${DueInvoiceValue}", String.valueOf(dueAmount))
                .replace("${netTotal}", String.valueOf(formatIndianAmount(totalAmountWoGst + logBaseAmount.doubleValue())))
                .replace("${totalGstAmount}", String.valueOf(formatIndianAmount(totalGst + logGstAmount.doubleValue())))
                .replace("${totalInvoiceValue}", String.valueOf(formatIndianAmount(totalAmountWithGst + deliveryCharges)))
                .replace("${paymentTransactionId}", orderInfo.getPaymentId())
                .replace("${paymentMode}", "ONLINE")
                .replace("${paymentDateTime}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")))
                .replace("${OrdDateTime}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")))
                .replace("${amountInWords}", GlobalValidator.convertRupeesToWords(totalAmountWithGst + deliveryCharges))
                .replace("${base64String}", Base64.getEncoder().encodeToString(baos.toByteArray()));

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(byteArrayOutputStream, true);

            byte[] contentBytes = byteArrayOutputStream.toByteArray();
            Long contentLength = Long.valueOf(contentBytes.length);


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
            emailService.sendEmailWithAttachement(EmailDetails.builder()
                    .msgBody("Rhokart invoice PDF attached here.\n\n ORDER ID: "+ orderInfo.getOrderId() + "\n Invoice ID: "+ "RHOBUY".concat(orderInfo.getOrderId()) +
                            "\n\n" +
                            "\n\n Regards\nRhoselect Private limited")
                    .toAddress(userDetailsDTO.getEmail())
                    .subject("Rhokart invoice for ORDER ID:"+orderInfo.getOrderId() + " Invoice no.: "+ "RHOBUY".concat(request.getOrderId()))
                    .filename(fileName).build());


            log.info("Pdf created -> ".concat(fileName));
            return fileName;
        } catch (Exception e) {
            log.error("Error creating PDF: {}", e.getMessage(), e);
            return null;
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
}
