package ch.mgb.younique.renamer.model;

import ch.mgb.younique.renamer.exceptions.DirectoryException;
import ch.mgb.younique.renamer.exceptions.ExcelException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenamerModel {
    public String conclusionMessage = "";

    public Integer excelRowStart = 1;
    public Integer excelRowEnd = 99;
    public Integer excelColA = 0;
    public Integer excelColB = 1;

    public String[] fileExtensions = new String[] {"jpg", "jpeg", "jpe", "jif", "jfif", "jfi", "png", "gif", "webp", "tiff", "tif", "psd", "raw", "arw", "cr2", "nrw", "k25", "bmp", "dib", "heif", "heic", "ind", "indd", "indt", "jp2", "j2k", "jpf", "jpx", "jpm", "mj2", "svg", "svgz", "ai", "eps", "pdf"};

    public void startRenaming(File excel, File directory) throws ExcelException, DirectoryException, IOException{
        Map<Integer, List<String>> excelData = new HashMap<>();
        try {
            excelData = getExcelData(excel);
        }catch (IOException e){
            throw new ExcelException("Die Excel-Daten konnten nicht gelesen werden.");
        }
        if (validateDirectory(directory) && validateExcel(excelData)){

        }

        //TODO go on

    }

    private boolean validateDirectory(File directory) throws DirectoryException, IOException {
        String pattern = makeFileNamePattern(fileExtensions);

        if (directory.exists()) {
            if (directory.isDirectory()) {
                File[] directoryListing = directory.listFiles();
                if (directoryListing != null && directoryListing.length > 0) {
                    for (File file : directoryListing) {
                        if (file.getName().matches(pattern))
                            return true;
                    }
                } else {
                    throw new DirectoryException("Das Verzeichnis ist leer.");
                }
            } else {
                throw new DirectoryException("Dies ist kein Verzeichnis.");
            }
        }else{
            throw new DirectoryException("Das Verzeichnis existiert nicht");
        }
        return false;
    }

    private Map<Integer, List<String>> getExcelData(File excel) throws ExcelException, IOException {
        FileInputStream file = new FileInputStream(excel);
        Workbook workbook = new XSSFWorkbook(file);

        Sheet sheet = workbook.getSheetAt(0);
        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        int y = 0;
        for (Row row : sheet){
            if (i >= excelRowStart && i <= excelRowEnd) {
                data.put(i, new ArrayList<String>());
                y = 0;
                for (Cell cell : row) {
                    if (y == excelColA )
                    switch (cell.getCellType()) {
                        case STRING:
                            data.get(i).add(cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                data.get(i).add(cell.getDateCellValue().toString());
                            } else {
                                data.get(i).add(Double.toString(cell.getNumericCellValue()));
                            }
                            break;
                        case BOOLEAN:
                            data.get(i).add(Boolean.toString(cell.getBooleanCellValue()));
                            break;
                        case FORMULA:
                            data.get(i).add(cell.getCellFormula());
                            break;
                        default:
                            data.get(i).add("");
                    }
                    y++;
                }
                i++;
            }
        }
        return data;
    }

    private boolean validateExcel(Map<Integer, List<String>> excelData) throws ExcelException{
        for (int i = excelRowStart; i <= excelRowEnd; i++){
            if (excelData.get(i).get(excelColA) == "") {
                throw new ExcelException("In der ersten Spalte gibt es ein leeres Feld");
            }
        }
        return true;
    }

    private String makeFileNamePattern(String[] extensions){
        String patternStart = "(.*/)*.+\\.(";
        String patternEnd = ")$";
        String patternStr = patternStart;
        for (String extension : extensions){
            patternStr = patternStr + extension + "|";
        }
        return patternStr.replaceFirst(".$","") + patternEnd;
    }
}
