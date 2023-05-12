package school.faang.user_service.util;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.exception.FileException;

public class PicResizeUtil {
    private static final int PROFILE_PIC_SIDE_BIG = 1080;
    private static final int PROFILE_PIC_SIDE_SMALL = 170;

    public static byte[] resizePictureToSmall(byte[] pic, String picFormat) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(pic));
            Image scaledImage = image.getScaledInstance(PROFILE_PIC_SIDE_SMALL, PROFILE_PIC_SIDE_SMALL, Image.SCALE_SMOOTH);

            BufferedImage resizedImage = new BufferedImage(PROFILE_PIC_SIDE_SMALL, PROFILE_PIC_SIDE_SMALL, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(scaledImage, 0, 0, PROFILE_PIC_SIDE_SMALL, PROFILE_PIC_SIDE_SMALL, null);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, picFormat, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new FileException(ErrorMessage.FILE_EXCEPTION);
        }
    }

    public static byte[] cropPicture(byte[] pic, String picFormat) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(pic));

            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();

            int offsetX = (originalWidth - PROFILE_PIC_SIDE_BIG) / 2;
            int offsetY = (originalHeight - PROFILE_PIC_SIDE_BIG) / 2;

            BufferedImage croppedImage = new BufferedImage(PROFILE_PIC_SIDE_BIG, PROFILE_PIC_SIDE_BIG, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = croppedImage.createGraphics();

            g.drawImage(image, 0, 0, PROFILE_PIC_SIDE_BIG, PROFILE_PIC_SIDE_BIG, offsetX, offsetY,
                    offsetX + PROFILE_PIC_SIDE_BIG, offsetY + PROFILE_PIC_SIDE_BIG, null);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(croppedImage, picFormat, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new FileException(ErrorMessage.FILE_EXCEPTION);
        }
    }
}