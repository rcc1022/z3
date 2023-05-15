package org.lpw.photon.office.excel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.lpw.photon.office.MediaWriter;
import org.lpw.photon.util.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.InputStream;

@Component("photon.office.excel.reader")
public class ExcelReaderImpl implements ExcelReader {
    @Inject
    private Logger logger;

    @Override
    public JSONObject read(InputStream inputStream, MediaWriter mediaWriter) {
        JSONObject object = new JSONObject();
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            JSONArray sheets = new JSONArray();
            workbook.forEach(sheet -> {
                JSONObject sheetj = new JSONObject();
                sheetj.put("name", sheet.getSheetName());
                sheetj.put("first", sheet.getFirstRowNum());
                sheetj.put("last", sheet.getLastRowNum());
                JSONArray rows = new JSONArray();
                sheet.forEach(row -> {
                    JSONObject rowj = new JSONObject();
                    int first = row.getFirstCellNum();
                    int last = row.getLastCellNum();
                    rowj.put("first", first);
                    rowj.put("last", last);
                    JSONArray cells = new JSONArray();
                    for (int i = first; i < last; i++) {
                        JSONObject cellj = new JSONObject();
                        Cell cell = row.getCell(i);
                        if (cell == null) {
                            cellj.put("type", "null");
                            cellj.put("value", "");
                        } else {
                            cellj.put("type", cell.getCellType().name().toLowerCase());
                            switch (cell.getCellType()) {
                                case STRING -> cellj.put("value", cell.getStringCellValue());
                                case NUMERIC -> cellj.put("value", cell.getNumericCellValue());
                                case BOOLEAN -> cellj.put("value", cell.getBooleanCellValue());
                                case FORMULA -> cellj.put("formula", cell.getCellFormula());
                                default -> cellj.put("value", "");
                            }
                        }
                        cells.add(cellj);
                    }
                    rowj.put("cells", cells);
                    rows.add(rowj);
                });
                sheetj.put("rows", rows);
                sheets.add(sheetj);
            });
            object.put("sheets", sheets);
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            logger.warn(e, "读取并解析Excel数据时发生异常！");
        }

        return object;
    }
}
