package com.jxnu.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @date 2020/4/19 16:53
 */
public class ImgUtil {

    /**
     * 调整图片比例，分辨率,并存入到硬盘
     *
     * @param in      指向一个图片的字节输入流
     * @param imgPath 要存入的文件夹
     * @param imgName 要存为的名字
     * @return false 因为图片分辨率太小，失败；true 成功
     * @throws IOException
     */
    public static Boolean reSize(InputStream in, String imgPath, String imgName) throws IOException {
        int minWidth, minHeight, maxWidth, maxHeight, realWidth, realHeight, w, h;
        //小图宽度
        minWidth = 320;
        //小图高度
        minHeight = 180;
        //大图宽度
        maxWidth = 800;
        //大图高度
        maxHeight = 450;

        BufferedImage bufferedImage = ImageIO.read(in);
        realWidth = bufferedImage.getWidth();
        realHeight = bufferedImage.getHeight();

        if (realWidth < minWidth || realHeight < minHeight) {
            return false;
        }

        //1.将图片变成16:9的
        if (9 * realWidth > 16 * realHeight) {
            w = 16 * realHeight / 9;
            h = realHeight;
        } else if (9 * realWidth < 16 * realHeight) {
            w = realWidth;
            h = 9 * realWidth / 16;
        } else {
            w = realWidth;
            h = realHeight;
        }
        bufferedImage = bufferedImage.getSubimage((realWidth - w) / 2, (realHeight - h) / 2, w, h);

        //2.生成小图并保存
        saveImage(minWidth, minHeight, bufferedImage, imgPath, "min" + imgName);

        //3.生成大图并保存（若原图裁剪成16:9后宽度大于maxWidth,则再裁剪成宽为maxWidth的，否则直接保存为大图）
        if (w < maxWidth) {
            ImageIO.write(bufferedImage, "jpg", new File(imgPath, imgName));
        } else {
            saveImage(maxWidth, maxHeight, bufferedImage, imgPath, imgName);
        }

        return true;
    }

    /**
     * 将图片等比缩小后并保存
     *
     * @param w             缩小的图片的宽度
     * @param h             缩小的图片的高度
     * @param bufferedImage 要保存的图片
     * @param imgPath       要存入的文件夹
     * @param imgName       要存为的名字
     * @throws IOException
     */
    private static void saveImage(int w, int h, BufferedImage bufferedImage, String imgPath, String imgName) throws IOException {
        //获取缩小后的Image对象
        Image minImage = bufferedImage.getScaledInstance(w, h, Image.SCALE_DEFAULT);
        //新建一个和minImage对象相同大小的画布
        BufferedImage minBufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        //获取画笔
        Graphics2D graphics = minBufferedImage.createGraphics();
        //将Image对象画在画布上,最后一个参数,ImageObserver:接收有关 Image 信息通知的异步更新接口,没用到直接传空
        graphics.drawImage(minImage, 0, 0, null);
        //释放资源
        graphics.dispose();
        //保存至硬盘
        ImageIO.write(minBufferedImage, "jpg", new File(imgPath, imgName));
    }

    /**
     * 删除图片
     *
     * @param imgPath 要删除的图片路径
     * @param imgName 要删除的图片名字
     */
    public static void deleteImage(String imgPath, String imgName) {
        File file = new File(imgPath, imgName);
        if (file.exists()){
            file.delete();
        }
        file = new File(imgPath, "min" + imgName);
        if (file.exists()){
            file.delete();
        }
    }
}
