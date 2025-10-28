package com.ming.pass.util;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.util.List;

@Slf4j
public class CustomCSVWriter {
    public static int write(final String filedName, List<String[]> data) {
        int rows = 0;
        try (CSVWriter writer = new CSVWriter(new FileWriter(filedName))) {
            writer.writeAll(data);
            rows = data.size();
        } catch (Exception e) {
            log.error("CustomCSVWriter - write: CSV 파일 생성 실패, fileName: {}", filedName);
        }
        return rows;
    }
}
