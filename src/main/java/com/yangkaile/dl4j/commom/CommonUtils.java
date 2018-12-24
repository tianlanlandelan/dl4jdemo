package com.yangkaile.dl4j.commom;

import org.nd4j.linalg.io.ClassPathResource;

import java.io.File;

/**
 * 通用工具类
 * @author yangkaile
 * @date 2018-12-19 14:33:30
 */
public class CommonUtils {
    public static void println(String text,Object object){
        System.out.println("====" + text + "===");
        System.out.println(object);
    }

    /**
     * 读取文件
     * @return
     */
    public static File getFile(String path){
        try {
            ClassPathResource classPathResource = new ClassPathResource(path);
            File directoryToLook = classPathResource.getFile();
            return directoryToLook;
        }catch (Exception e){
            return null;
        }
    }

}
