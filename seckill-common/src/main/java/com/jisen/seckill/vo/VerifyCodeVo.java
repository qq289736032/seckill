package com.jisen.seckill.vo;

import java.awt.image.BufferedImage;

/**
 * @author jisen
 * @date 2019/7/14 11:46
 */
public class VerifyCodeVo {
    private BufferedImage image;
    private int expResult;

    public VerifyCodeVo() {
    }

    public VerifyCodeVo(BufferedImage image, int expResult) {
        this.image = image;
        this.expResult = expResult;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public int getExpResult() {
        return expResult;
    }

    public void setExpResult(int expResult) {
        this.expResult = expResult;
    }
}
