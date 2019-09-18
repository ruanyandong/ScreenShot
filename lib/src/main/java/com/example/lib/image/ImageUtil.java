package com.example.lib.image;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public class ImageUtil {
    public static boolean validPng(String fileLocation)  {
        String suffix = fileLocation.substring(fileLocation.lastIndexOf(".") + 1);
        if(!("png").equals(suffix.toLowerCase())){
            //JPEG是有损格式,保存JPEG并重新加载后颜色将不会完全相同，GIF比较复杂暂不考虑
            return false;
        }
        File file = new File(fileLocation);
        InputStream is = null;
        FileInputStream fileInputStream=null;
        try {
            fileInputStream=new FileInputStream(file);
            is = new BufferedInputStream(fileInputStream);
            String mimeType = URLConnection.guessContentTypeFromStream(is);
            return ("image/png").equals(mimeType);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                fileInputStream.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
