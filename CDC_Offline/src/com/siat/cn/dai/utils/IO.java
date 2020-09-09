package com.siat.cn.dai.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class IO {
    public static List<String> readFileByLine(String strFile){
        try {
            File file = new File(strFile);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String strLine = null;
            List<String> res = new ArrayList<>();
            while(null != (strLine = bufferedReader.readLine())){
                res.add(strLine);
            }
            return res;
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
