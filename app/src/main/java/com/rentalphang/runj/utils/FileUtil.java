package com.rentalphang.runj.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件工具类
 * Created by 洋 on 2016/4/25.
 */
public class FileUtil {


    /**
     * 一级目录 rungang
     */
    public static String  FIRST_DIR_PATH = Environment.getExternalStorageDirectory().getPath()
            +File.separator+"rungang";

    /**
     * 二级目录 图片images
     */
    private static String  IMAGES_DIR_PATH = FIRST_DIR_PATH+File.separator+"images";



    /**
     * 保存图片
     * @param bitmap
     * @param filePath
     * @return 保存路径
     */
    public static String saveBitmapToFile(Bitmap bitmap, String filePath){
        String picPath=null;
        FileOutputStream fileOutputStream = null;
        try {

            // 新建一级目录
            File firstDir = new File(FIRST_DIR_PATH);
            if(!firstDir.exists()) {
                firstDir.exists();
            }
            //建立二级图片目录
            File imagedir = new File(IMAGES_DIR_PATH);
            if (! imagedir.exists()) {
                imagedir.mkdir();

            }
            //三级分类目录
            String saveDir = IMAGES_DIR_PATH+File.separator+filePath;
            File dir = new File(saveDir);
            if(!dir.exists()) {
                dir.mkdirs();
            }
           // 生成文件名
            SimpleDateFormat t = new SimpleDateFormat("yyyyMMddssSSS");
            String fileName = filePath + (t.format(new Date()))+".png";
            Log.i("TAG","文件名"+fileName);
            // 新建文件
            File file = new File(saveDir, fileName);
            // 打开文件输出流
            fileOutputStream = new FileOutputStream(file);
            Log.i("TAG","文件输出流");
            // 生成图片文件
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            // 相片的完整路径
            picPath = file.getPath();

            Log.i("TAG","图片存储成功"+picPath);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            if (fileOutputStream != null) {
                try {
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        return  picPath;
    }

    /**
     * 根据路径，从文件中获取图片
     * @param path
     * @return
     */
    public static Bitmap getBitmapFromFile(String path) {

        Bitmap bitmap = null;

        File file = new File(path);

        if(file.exists()) {
            bitmap = BitmapFactory.decodeFile(path);
        }

        return bitmap;
    }

    /**
     * 根据路径，删除图片
     * @param path
     */
    public static void deleteBitmapByPath(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }


}
