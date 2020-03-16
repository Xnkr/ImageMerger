package image.merge;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<BufferedImage> images = new ArrayList<>();
        List<File> files = selectImages();
        if (!files.isEmpty()) {
            for (File file : files) {
                try {
                    images.add(ImageIO.read(file));
                } catch (IOException e) {
                    System.out.println("Something went wrong while loading images " + e.getMessage());
                    return;
                }
            }
            BufferedImage outImage = joinBufferedImage(images);
            File saveFile = chooseSaveFile();
            try {
                ImageIO.write(outImage, "png", saveFile);
                System.out.println("Success");
            } catch (IOException e) {
                System.out.println("Something went wrong while creating new image " + e.getMessage());
                return;
            }
        } else {
            System.out.println("No files were chosen");
        }
    }

    private static File chooseSaveFile() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());


        int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            return new File(addFileExtIfNecessary(file.getAbsolutePath(), ".png"));
        }
        return null;
    }

    private static String addFileExtIfNecessary(String file, String ext) {
        if (file.lastIndexOf('.') == -1)
            file += ext;

        return file;
    }


    private static List<File> selectImages() {
        int returnValue = 0;
        int i = 1;
        List<File> files = new ArrayList<>();
        while (returnValue == JFileChooser.APPROVE_OPTION) {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setDialogTitle("Select Image " + i++);
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG and JPEG images", "jpeg", "png", "jpg");
            jfc.addChoosableFileFilter(filter);

            returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = jfc.getSelectedFile();
                files.add(file);
            }
        }
        return files;
    }

    public static BufferedImage joinBufferedImage(List<BufferedImage> imgs) {
        int offset = 2;
        int width = offset;
        int height = offset;
        int maxWidth = 0;
        for (BufferedImage image : imgs) {
            height += image.getHeight();
            maxWidth = Math.max(image.getWidth(), maxWidth);
        }
        width += maxWidth;
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setColor(oldColor);
        int prevHeight = 0;
        for (int i = 0; i < imgs.size(); i++) {
            BufferedImage img = imgs.get(i);
            if (i == 0) {
                g2.drawImage(img, null, 0, 0);
            } else {
                g2.drawImage(img, null, 0, prevHeight);
            }
            prevHeight += img.getHeight() + offset;
        }
        g2.dispose();
        return newImage;
    }
}
