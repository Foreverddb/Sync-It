package com.example.sync_everything.util;


import com.example.sync_everything.entity.verification.Code;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

/**
 * @author ForeverDdB
 * @ClassName VerificationCodeUtil
 * @Description 验证码生成图片工具类
 * @createTime 2022年 09月19日 21:31
 **/
public class VerificationCodeUtil {
    private VerificationCodeUtil (){}

    /**
     * 生成验证码的范围：a-zA-Z0-9
     *      去掉0(数字)和O(拼音)容易混淆的(小写的1和L也可以去掉,大写不用了)
     */
    private final static char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public final static int CODE_COUNT = 5;

    /**
     * 随机生成n个验证码的字符串和其图片方法
     *  思路：
     *  1、根据宽高创建 BufferedImage图片对象
     *  2、获取图片对象的画笔对象Graphics
     *  3、画笔画入数据（背景色，边框，字体，字体位置，颜色等）
     *  4、最后通过ImageIO.write()方法将图片对象写入OutputStream
     * @param width - 图片宽度
     * @param height - 图片高度
     * @return  String - 将随机生成的CODE_COUNT个验证码以字符串返回
     */
    public static Code generateVerificationCode(int width, int height) throws IOException {
        // 1、根据宽高创建 BufferedImage图片对象
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 2、获取图片对象的画笔对象Graphics
        Graphics2D graphics = bufferedImage.createGraphics();
        // 3、画笔画入数据（背景色，边框，字体，字体位置，颜色等）
        // 背景色
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        //边框颜色
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0, 0, width-1, height-1);
        // 字体
        Font font = new Font("Fixedsys", Font.PLAIN, height - 2);
//        Font font = new Font("微软雅黑", Font.ROMAN_BASELINE, height - 2);
        graphics.setFont(font);

        // 添加干扰线：坐标/颜色随机
        Random random = new Random();
        for (int i = 0; i < (CODE_COUNT * 2); i++) {
            graphics.setColor(getRandomColor());
            graphics.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
        }
        // 添加噪点:
        for(int i = 0;i < (CODE_COUNT * 3);i++){
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            graphics.setColor(getRandomColor());
            graphics.fillRect(x, y, 2,2);
        }

        // 画随机数：颜色随机，宽高自定义
        StringBuilder randomCode = new StringBuilder();
        int charWidth = width / (CODE_COUNT + 2);
        int charHeight = height - 5;
        // 随机产生CODE_COUNT个字符的验证码。
        for (int i = 0; i < CODE_COUNT; i++) {
            int x = (i + 1) * charWidth;
            String strRandom = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
            randomCode.append(strRandom);
            graphics.setColor(getRandomColor());
            //设置字体旋转角度
            int degree = random.nextInt() % 30;  //角度小于30度
            graphics.rotate(degree * Math.PI / 180, x, 45);  //正向旋转
            graphics.drawString(strRandom, x, charHeight);
            graphics.rotate(-degree * Math.PI / 180, x, 45); //反向旋转
        }
        // 4、最后通过ImageIO.write()方法将图片对象写入OutputStream
//        ImageIO.write(bufferedImage,imgFormat,outputStream);
        Code code = new Code();
        code.setCode(randomCode.toString());
        code.setCodeImage(getBase64FromImage(bufferedImage));
        return code;
    }

    /**
     * 随机取色
     */
    private static Color getRandomColor() {
        Random ran = new Random();
        return new Color(ran.nextInt(256),ran.nextInt(256), ran.nextInt(256));
    }

    //BufferedImage 转base64
    public static String getBase64FromImage(BufferedImage img){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            // 设置图片的格式
            ImageIO.write(img, "jpg", stream);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] bytes = Base64.encodeBase64(stream.toByteArray());
        String base64 = new String(bytes);
        return  "data:image/jpeg;base64,"+base64;
    }

    public static void main(String[] args) {
        try {
            Code code = VerificationCodeUtil.generateVerificationCode(200,50);
            System.out.println(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
