package com.bboynobita.mypuzzle.utils;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bboynobita on 2018/1/5.
 * 工具类，把一张大图切成若干张小图
 */

public class ImageSplitterUtil {
    /**
     *
     * @param bitmap 图片
     * @param piece 指定每一行图片的块数
     * @return List<ImagePiece>
     */
    public static List<ImagePiece> splitImage(Bitmap bitmap,int piece){
        List<ImagePiece > imagePieces=new ArrayList<ImagePiece>();

        int width=bitmap.getWidth();
        int height=bitmap.getHeight();

        //每一块图片的宽度，因为是正方形所以宽高相同
        int pieceWidth=Math.min(width,height)/piece;
        //切图
        for (int i=0;i<piece;i++){
            for (int j=0;j<piece;j++){
                ImagePiece imagePiece=new ImagePiece();
                imagePiece.setIndex(j+i*piece);
                int x=j*pieceWidth;
                int y=i*pieceWidth;
                imagePiece.setBitmap(Bitmap.createBitmap(bitmap,x,y,pieceWidth,pieceWidth));
                imagePieces.add(imagePiece);
            }
        }
        return imagePieces;
    }
}
