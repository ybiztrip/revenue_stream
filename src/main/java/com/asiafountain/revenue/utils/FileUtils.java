package com.asiafountain.revenue.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static List<String> loadData(String filePath) {
        List<String> result = new ArrayList<>();

        try {

            String data = null;

            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);

            while ((data = br.readLine()) != null) {
                result.add(data);
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
