package com.rentalphang.runj.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;


/**
 * 文件操作类
 * @author Administrator
 *
 */
public class FileUtils {
    public static String SDCardPathRoot = Environment.getExternalStorageDirectory()+ File.separator;
    //public static String memoryCardPathRoot = Environment.getInternalStorageDirectory() + File.separator;

    /**
     * 在SD卡上创建文件
     * fileName 文件名称
     * dir 在sdcard的哪个目录下面创建文件
     *
     * @throws IOException
     */
    public static File creatSDFile(String dir,String fileName) throws IOException {
        File file = new File(SDCardPathRoot + dir + File.separator + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public static File creatSDDir(String dirName) {
        File dir = new File(SDCardPathRoot + dirName + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }



    /**
     * 判断SD卡上的文件夹是否存在
     */
    public static boolean isSDFileExist(String dir,String fileName) {
        File file = new File(SDCardPathRoot + dir + File.separator + fileName);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SDCard中
     */
    public static File write2SDFromInput(String dir, String fileName,InputStream inputStream) {
        File file = null; 			// 声明文件对象
        OutputStream output = null; // 声明输出流对象
        try {
            creatSDDir(dir); 		// 在SDCard中创建目录
            file = creatSDFile(dir,fileName); // 在SDCard中创建文件
            output = new FileOutputStream(file); // 将文件放入到输出流中
            int temp;
            byte buffer[] = new byte[4 * 1024];
            while ((temp = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, temp);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }



    /**
     * 向/data/data/com.tearCloth/files目录下读取数据
     * @param context
     * @param file
     * @return
     */
    public static String read(Context context, String fileName) {
        String data = "";
        try {
            FileInputStream stream = context.openFileInput(fileName);
            StringBuffer sb = new StringBuffer();
            int c;
            while ((c = stream.read()) != -1) {
                sb.append((char) c);
            }
            stream.close();
            data = sb.toString();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return data;
    }



    /**
     * 向/data/data/com.tearCloth/files目录下写入数据
     * @param context
     * @param file
     * @param msg
     */
    public static void write(Context context, String fileName, String msg) {
        try {
            FileOutputStream stream = context.openFileOutput(fileName,Context.MODE_PRIVATE);
            stream.write(msg.getBytes());
            stream.flush();
            stream.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }


    /**
     * 删除目录下的文件
     * @param file
     */
    public static void deleteTheFile(File file) {
        if (file.exists()) {
            if (file.isFile()) { // 判断是否是文件
                file.delete();
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteTheFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            System.out.println("文件不存在！"+"\n");
        }
    }


    /**
     * 遍历目录得到目录下的文件
     * @param path	文件路径
     * @return
     */
    public static List<String> getFile(String path)
    {
        File file = new File(path);
        File[] childFiles = file.listFiles();	//得到所有子目录或文件，
        List<String> filename=new ArrayList<String>();
        for(File files:childFiles){
            String myfilename=files.getName();//得到文件名称
            filename.add(myfilename);
        }
        return filename;
    }



    /**
     * 取得文件大小
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSizes(File f) throws Exception{
        long s=0;
        if (f.exists()) {
            @SuppressWarnings("resource")
            FileInputStream fis = new FileInputStream(f);
            s= fis.available();
        } else {
            f.createNewFile();
            System.out.println("文件不存在");
        }
        return s;
    }



    /**
     * 取得文件夹大小
     * @param f
     * @return
     * @throws Exception
     */
    // 递归
    public static long getFileSize(File f)throws Exception
    {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++)
        {
            if (flist[i].isDirectory())
            {
                size = size + getFileSize(flist[i]);
            } else
            {
                size = size + flist[i].length();
            }
        }
        return size;
    }



    /**
     * 转换文件大小
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 判断SD卡是否存在
     * @return
     */
    public static  boolean isExistSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }


    /**
     * 在内存卡上创建目录
     *
     * @param dirName
     */
    public static File creatMemoryDir(String dirName) {
        File dir = new File(dirName + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 判断内存卡上的文件夹是否存在
     */
    public static boolean isMemoryFileExist(String dirName,String fileName) {
        File file = new File(dirName + File.separator + fileName);
        return file.exists();
    }

    /**
     * 在SD卡上创建文件
     * fileName 文件名称
     * dir 在sdcard的哪个目录下面创建文件
     *
     * @throws IOException
     */
    public static File creatMemoryFile(String dir,String fileName) throws IOException {
        File file = new File(dir + File.separator + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 将一个InputStream里面的数据写入到中
     */
    public static File write2MemoryFromInput(String dir, String fileName,InputStream inputStream) {
        File file = null; 			// 声明文件对象
        OutputStream output = null; // 声明输出流对象
        try {
            creatMemoryDir(dir);
            file = creatMemoryFile(dir,fileName);
            output = new FileOutputStream(file); // 将文件放入到输出流中
            int temp;
            byte buffer[] = new byte[4 * 1024];
            while ((temp = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, temp);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    /**
     * 向sdcard中写入文件
     * @param filename 文件名
     * @param content 文件内容
     */
    public static void saveToSDCard(String filename,String content) throws Exception{
        File file=new File(Environment.getExternalStorageDirectory(), filename);
        OutputStream out=new FileOutputStream(file);
        out.write(content.getBytes());
        out.close();
    }


    /**
     * 一级目录 runj
     */
    public static String  FIRST_DIR_PATH = Environment.getExternalStorageDirectory().getPath()
            +File.separator+"runj";

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