package com.example.apitestmanager.atm.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.json.JsonObject;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class AppUtils {

    public List<Map<String, Object>> readExcel(String filePath) throws IOException {
        List<Map<String, Object>> data = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming first sheet
            for (Row row : sheet) {
                if (isRowEmpty(row)) {
                    break;
                }
                Map<String, Object> rowData = new HashMap<>();
                List<String> columns = List.of("Base URL", "Endpoint", "Method", "Header", "Request Body", "Response Body");
                for(int i=0; i<columns.size(); i++){
                    Cell cell = row.getCell(i);
                    Object cellValue = null;
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                cellValue = cell.getDateCellValue();
                            } else {
                                cellValue = cell.getNumericCellValue();
                            }
                            break;
                        case STRING:
                            cellValue = cell.getStringCellValue();
                            break;
                        case BOOLEAN:
                            cellValue = cell.getBooleanCellValue();
                            break;
                        case FORMULA:
                            cellValue = cell.getCellFormula();
                            break;
                        case BLANK:
                            cellValue = "";
                            break;
                        case ERROR:
                            cellValue = "Error";
                            break;
                        default:
                            cellValue = "Unknown Cell Type";
                    }
                    rowData.put(columns.get(i), cellValue);
                }
                data.add(rowData);
            }
        }
        return data;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    public JsonObject convertStringToJsonObject(String json) {
        return new JsonObject(json);
    }

    public static String objectToJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

}
