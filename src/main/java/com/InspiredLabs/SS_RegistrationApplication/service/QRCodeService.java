package com.InspiredLabs.SS_RegistrationApplication.service;

import com.InspiredLabs.SS_RegistrationApplication.service.qrcodeGenetator.QrCode;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import static com.InspiredLabs.SS_RegistrationApplication.utils.Constant.IMAGE_NAME;
import static com.InspiredLabs.SS_RegistrationApplication.utils.Constant.VERIFICATION_CODE;

@Service
public class QRCodeService {

    private static final Logger LOGGER = Logger.getLogger(QRCodeService.class.getName());


    public void generateQRCode(Map<String, String> verificationDetails) throws IOException {

        LOGGER.info("VerificationCode: " + verificationDetails.get(VERIFICATION_CODE));
        LOGGER.info("Image name : " + verificationDetails.get(IMAGE_NAME));

        QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;  // Error correction level

        QrCode qr = QrCode.encodeText(verificationDetails.get(VERIFICATION_CODE), errCorLvl);  // Make the QR Code symbol

        BufferedImage img = toImage(qr, 10, 4);          // Convert to bitmap image
        String fileName = verificationDetails.get(IMAGE_NAME);
        LOGGER.info("*******QRCODE file name is: " + fileName);

        File imgFile = new File(fileName);
        LOGGER.info("File location ==================    " + imgFile.getAbsolutePath());

        ImageIO.write(img, "png", imgFile);              // Write image to file

        String svg = toSvgString(qr, 4, "#FFFFFF", "#000000");  // Convert to SVG XML code
        File svgFile = new File("hello-world-QR.svg");          // File path for output

        Files.write(svgFile.toPath(),                           // Write image to file
                svg.getBytes(StandardCharsets.UTF_8));


    }

    private static BufferedImage toImage(QrCode qr, int scale, int border, int lightColor, int darkColor) {
        Objects.requireNonNull(qr);
        if (scale <= 0 || border < 0)
            throw new IllegalArgumentException("Value out of range");
        if (border > Integer.MAX_VALUE / 2 || qr.size + border * 2L > Integer.MAX_VALUE / scale)
            throw new IllegalArgumentException("Scale or border too large");

        BufferedImage result = new BufferedImage((qr.size + border * 2) * scale, (qr.size + border * 2) * scale, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                boolean color = qr.getModule(x / scale - border, y / scale - border);
                result.setRGB(x, y, color ? darkColor : lightColor);
            }
        }
        return result;
    }

    private static BufferedImage toImage(QrCode qr, int scale, int border) {
        return toImage(qr, scale, border, 0xFFFFFF, 0x000000);
    }

    private static String toSvgString(QrCode qr, int border, String lightColor, String darkColor) {
        Objects.requireNonNull(qr);
        Objects.requireNonNull(lightColor);
        Objects.requireNonNull(darkColor);
        if (border < 0)
            throw new IllegalArgumentException("Border must be non-negative");
        long brd = border;
        StringBuilder sb = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n")
                .append(String.format("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"0 0 %1$d %1$d\" stroke=\"none\">\n",
                        qr.size + brd * 2))
                .append("\t<rect width=\"100%\" height=\"100%\" fill=\"" + lightColor + "\"/>\n")
                .append("\t<path d=\"");
        for (int y = 0; y < qr.size; y++) {
            for (int x = 0; x < qr.size; x++) {
                if (qr.getModule(x, y)) {
                    if (x != 0 || y != 0)
                        sb.append(" ");
                    sb.append(String.format("M%d,%dh1v1h-1z", x + brd, y + brd));
                }
            }
        }
        return sb
                .append("\" fill=\"" + darkColor + "\"/>\n")
                .append("</svg>\n")
                .toString();
    }

}
