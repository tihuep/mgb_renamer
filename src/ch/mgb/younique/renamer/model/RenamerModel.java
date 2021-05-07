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

    public Integer excelRowStart = 0;
    public Integer excelRowEnd = 0;
    public Integer excelColA = 0;
    public Integer excelColB = 0;

    public String[] fileExtensions = new String[] {"jpg", "jpeg", "jpe", "jif", "jfif", "jfi", "png", "gif", "webp", "tiff", "tif", "psd", "raw", "arw", "cr2", "nrw", "k25", "bmp", "dib", "heif", "heic", "ind", "indd", "indt", "jp2", "j2k", "jpf", "jpx", "jpm", "mj2", "svg", "svgz", "ai", "eps", "pdf"};

    public void startRenaming(File excel, File directory) throws ExcelException, DirectoryException, IOException{
        Map<Integer, String[]> excelData = new HashMap<>();
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
            throw new DirectoryException("Das Verzeichnis existiert nicht.");
        }
        return false;
    }

    private boolean validateExcel(Map<Integer, String[]> excelData) throws ExcelException{
        for (int i = excelRowStart; i <= excelRowEnd; i++){
            if (excelData.get(i)[0].equals("") && excelData.get(i)[1].equals("")) {
                throw new ExcelException("In Zeile " + (i + 1) + " sind beide Felder leer." );
            }
            if (excelData.get(i)[0].equals("")){
                throw new ExcelException("In Zeile " + (i + 1) + " ist das erste Feld leer." );
            }
            if (excelData.get(i)[1].equals("")){
                throw new ExcelException("In Zeile " + (i + 1) + " ist das zweite Feld leer." );
            }
        }
        return true;
    }

    private Map<Integer, String[]> getExcelData(File excel) throws ExcelException, IOException {
        FileInputStream file = new FileInputStream(excel);
        Workbook workbook = new XSSFWorkbook(file);

        Sheet sheet = workbook.getSheetAt(0);
        Map<Integer, String[]> data = new HashMap<>();
        int i = 0;
        int j = 0;
        for (Row row : sheet){
            if (i >= excelRowStart && i <= excelRowEnd) {
                data.put(i, new String[2]);
                j = 0;
                for (Cell cell : row) {
                    if (j == excelColA || j == excelColB) {
                        switch (cell.getCellType()) {
                            case STRING:
                                data.get(i)[j == excelColA ? 0 : 1] = cell.getStringCellValue();
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    data.get(i)[j == excelColA ? 0 : 1] = cell.getDateCellValue().toString();
                                } else {
                                    data.get(i)[j == excelColA ? 0 : 1] = String.format("%.0f", cell.getNumericCellValue());
                                }
                                break;
                            case BOOLEAN:
                                data.get(i)[j == excelColA ? 0 : 1] = Boolean.toString(cell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                data.get(i)[j == excelColA ? 0 : 1] = cell.getCellFormula();
                                break;
                            default:
                                data.get(i)[j == excelColA ? 0 : 1] = "";
                        }
                    }
                    j++;
                }
            }
            i++;
        }
        return data;
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
