package com.asiafountain.revenue.utils;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

@Log4j2
public class StringUtil {
    public static String unzip(byte[] responseBytes) throws Exception {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(responseBytes);
            GZIPInputStream gzis = new GZIPInputStream(bais);
            InputStreamReader reader = new InputStreamReader(gzis);
            BufferedReader in = new BufferedReader(reader);

            String readed;
            StringBuilder stringBuilder = new StringBuilder();
            while ((readed = in.readLine()) != null) {
                stringBuilder.append(readed);
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            log.error("Failed to Unzip Data", e);
        }
        return "{}";
    }
}
