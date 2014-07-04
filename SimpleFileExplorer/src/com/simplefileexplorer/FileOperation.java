package com.simplefileexplorer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileOperation {

    public static boolean copyNotCoverExistFile(File src, File dst){
        while(dst.exists()){
            dst = new File(dst.getParent(), "copy-" +dst.getName());
        }
        if(src.isFile()){
            return copyFile(src, dst);
        } else {
            return copyDirectory(src, dst);
        }
    }
    
    public static boolean copyFile(File src, File dst){
        try{
            if(!dst.exists()&&(!dst.createNewFile()||!dst.canWrite())){
                return false;
            }
            InputStream  in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }catch(IOException e){
            return false;
        }
        return true;
    }
    
    public static boolean copyDirectory(File src, File dst){
        boolean flag = true;
        if(src.isDirectory()){
            if(!dst.exists()&&(!dst.mkdir()||!src.canExecute())){
                return false;
            } else {
                for(File f: src.listFiles()){
                    flag = flag && copyDirectory(f, new File(dst.getPath() + "/" + f.getName()));
                }
            }
        } else{
            flag = copyFile(src, dst);
        }
        return flag;
    }
    
    public static boolean deleteFile(File srcFile){
        boolean b = true;
        if(srcFile.isDirectory()){
            for(File f : srcFile.listFiles()){
                b = b && deleteFile(f);
            }
        }
        if(b){
            return srcFile.delete();
        } else {
            return b;
        }
    }
    
}
