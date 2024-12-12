package org.example;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import javax.imageio.ImageIO;

import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

public class ImageCombinerGUI extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(ImageCombinerGUI.class.getName());
    private final JButton fileChooserButton = new JButton("选择本地文件");
    private final JFileChooser fileChooser = new JFileChooser();
    private final FileFilter fileFilter = new ImageFileFilter();
    private final JButton cropButton = new JButton("截取中间正方形");
    private final JButton combineButton = new JButton("合成图像");
    private String localImagePath;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImageCombinerGUI::new);
    }

    public ImageCombinerGUI() {
        setTitle("图像合成工具");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel();

        // 设置文件选择按钮的点击事件
        fileChooserButton.addActionListener(this::handleFileChooserButtonClick);

        cropButton.setEnabled(false);
        // 设置裁剪按钮的点击事件
        cropButton.addActionListener(this::handleCropButtonClick);

        combineButton.setEnabled(false);
        // 设置合成按钮的点击事件
        combineButton.addActionListener(this::handleCombineButtonClick);

        panel.add(fileChooserButton);
        panel.add(cropButton);
        panel.add(combineButton);
        add(panel);
    }

    // 处理文件选择按钮点击事件的方法
    private void handleFileChooserButtonClick(java.awt.event.ActionEvent e) {
        fileChooser.setFileFilter(fileFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            localImagePath = file.getAbsolutePath();
            cropButton.setEnabled(true);
            combineButton.setEnabled(false);
            JOptionPane.showMessageDialog(null, "文件选择成功，可进行下一步操作（裁剪图像）", "提示", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "文件选择操作取消", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // 处理裁剪按钮点击事件的方法
    private void handleCropButtonClick(java.awt.event.ActionEvent e) {
        try {
            BufferedImage originalImage = readImage(localImagePath);
            BufferedImage croppedImage = cropToSquare(originalImage);
            saveImage(croppedImage, localImagePath);
            combineButton.setEnabled(true);
            JOptionPane.showMessageDialog(null, "图片截取成功！可进行图像合成操作", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            handleImageOperationException("图片截取出现错误", ex);
        }
    }

    // 处理合成按钮点击事件的方法
    private void handleCombineButtonClick(java.awt.event.ActionEvent e) {
        try {
            combineImages();
            JOptionPane.showMessageDialog(null, "图像合成成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            handleImageOperationException("图像合成出现错误", ex);
        }
    }

    // 读取图像文件的方法
    private BufferedImage readImage(String imagePath) throws IOException {
        return ImageIO.read(new File(imagePath));
    }

    // 保存图像文件的方法
    private void saveImage(BufferedImage image, String outputPath) throws IOException {
        File outputFile = new File(outputPath);
        ImageIO.write(image, "png", outputFile);
    }

    // 处理图像操作相关异常的方法，统一处理并给用户提示
    private void handleImageOperationException(String errorMessage, IOException ex) {
        LOGGER.log(Level.SEVERE, errorMessage + ": " + ex.getMessage(), ex);
        JOptionPane.showMessageDialog(null, errorMessage + ": " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
    }

    private BufferedImage cropToSquare(BufferedImage originalImage) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int size = Math.min(originalWidth, originalHeight);
        int x = (originalWidth - size) / 2;
        int y = (originalHeight - size) / 2;
        BufferedImage squareImage = originalImage.getSubimage(x, y, size, size);

        // 创建一个具有透明背景的新图像，类型为ARGB
        BufferedImage roundedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = roundedImage.createGraphics();
        g2d.setComposite(AlphaComposite.Src);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 设置圆角半径，这里设为边长的1/5
        int cornerRadius = size / 4;
        Shape roundedRectangle = new RoundRectangle2D.Float(0, 0, size, size, cornerRadius, cornerRadius);
        g2d.setClip(roundedRectangle);
        g2d.drawImage(squareImage, 0, 0, null);
        g2d.dispose();

        return roundedImage;
    }

    private void combineImages() throws IOException {
        BufferedImage localImage = readImage(localImagePath);
        int localImageHeight = localImage.getHeight();

        // 生成纯白背景的正方形图片
        int sideLength = (int) (localImageHeight * 1.138);
        BufferedImage whiteBackgroundImage = new BufferedImage(sideLength, sideLength, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2dBackground = whiteBackgroundImage.createGraphics();
        g2dBackground.setColor(Color.WHITE);
        g2dBackground.fillRect(0, 0, sideLength, sideLength);
        g2dBackground.dispose();

        int diameter = (int) (localImageHeight * 0.38);
        BufferedImage numberImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = numberImage.createGraphics();
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, diameter, diameter);
        g2d.setColor(new Color(250, 82, 81));
        g2d.fillOval(0, 0, diameter, diameter);
        g2d.setFont(new Font("Arial", Font.BOLD, (int) (diameter * 0.7)));
        g2d.setColor(Color.WHITE);
        FontMetrics fontMetrics = g2d.getFontMetrics();

        // 计算数字的宽度和高度
        int fontWidth = fontMetrics.stringWidth("1");
        int fontHeight = fontMetrics.getHeight();

        // 更精确地计算数字在圆形中心的位置
        int x = (diameter - fontWidth) / 2;
        int y = (diameter - fontHeight) / 2 + fontMetrics.getAscent();
        g2d.drawString("1", x, y);
        g2d.dispose();

        int numberWidth = numberImage.getWidth();

        Graphics2D g2dWhiteBackground = whiteBackgroundImage.createGraphics();
        // 将本地文件图片放置在纯白背景图的左下角
        g2dWhiteBackground.drawImage(localImage, 0, sideLength - localImageHeight, null);
        // 将带有数字1的图片放置在纯白背景图的右上角
        g2dWhiteBackground.drawImage(numberImage, sideLength - numberWidth, 0, null);
        g2dWhiteBackground.dispose();

        // 保存合成后的图片
        File outputFile = new File(new File(localImagePath).getParent() + File.separator + "created.png");
        saveImage(whiteBackgroundImage, outputFile.getAbsolutePath());
    }

    // 自定义文件过滤器类，用于只显示图像文件
    private static class ImageFileFilter extends FileFilter {
        final String[] fileExtension = {"jpg", "jpeg", "png", "bmp", "gif"};

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) return true;
            for (String s : fileExtension) if (f.getName().toLowerCase().endsWith(s)) return true;
            return false;
        }

        @Override
        public String getDescription() {
            return "图像文件 (*.jpg/jpeg,*.png,*.bmp,*.gif)";
        }
    }
}