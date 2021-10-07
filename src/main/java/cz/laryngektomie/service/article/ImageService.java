package cz.laryngektomie.service.article;

import cz.laryngektomie.model.article.Image;
import cz.laryngektomie.repository.article.ImageRepository;
import cz.laryngektomie.service.ServiceBase;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ImageService extends ServiceBase<Image> {

    private ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        super(imageRepository);
        this.imageRepository = imageRepository;
    }

    //Convert and resize MultipartFile to image
    public Image saveAndResizeImage(MultipartFile file) {
        if (Objects.equals(file.getContentType(), "image/jpeg") || file.getContentType().equals("image/png")) {

            try {
                BufferedImage originalImage =
                        ImageIO.read(new ByteArrayInputStream(file.getBytes()));
                BufferedImage resizedImage = resize(originalImage, 200, 200);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", baos);
                baos.flush();
                byte[] imageInByte = baos.toByteArray();
                baos.close();
                Image image = new Image();
                image.setData(imageInByte);
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                imageRepository.save(image);
                return image;

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    public Image saveImage(MultipartFile file) throws IOException {
        Image image = null;
        if (file != null) {
            if (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {
                image = new Image(file.getName(), file.getContentType(), file.getBytes());

                imageRepository.save(image);
            }
        }
        return image;
    }


    public List<Image> saveImages(List<MultipartFile> files) throws IOException {
        List<Image> images = new ArrayList<>();

        for (MultipartFile file : files) {
            Image image = null;
            if (file != null) {
                if (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {
                    image = new Image(file.getName(), file.getContentType(), file.getBytes());

                    imageRepository.save(image);
                }
            }
            images.add(image);
        }


        return images;
    }

    private static BufferedImage resize(BufferedImage img, int newW, int newH) {
        java.awt.Image tmp = img.getScaledInstance(newW, newH, java.awt.Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}
