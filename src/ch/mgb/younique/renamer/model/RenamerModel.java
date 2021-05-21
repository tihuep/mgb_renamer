package ch.mgb.younique.renamer.model;

import ch.mgb.younique.renamer.exceptions.DirectoryException;
import ch.mgb.younique.renamer.exceptions.ExcelException;
import javafx.scene.Parent;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RenamerModel {
    public Parent mainRoot = null;

    public String conclusionMessage = "";

    public Integer excelRowStart = 0;
    public Integer excelRowEnd = 0;
    public Integer excelColA = 0;
    public Integer excelColB = 0;

    public File selectedExcelFile = null;
    public File selectedImgDirectory = null;

    public String[] fileExtensions = new String[] {"jpg", "jpeg", "jpe", "jif", "jfif", "jfi", "png", "gif", "webp", "tiff", "tif", "psd", "raw", "arw", "cr2", "nrw", "k25", "bmp", "dib", "heif", "heic", "ind", "indd", "indt", "jp2", "j2k", "jpf", "jpx", "jpm", "mj2", "svg", "svgz", "ai", "eps", "pdf"};

    private List<String> renamedFiles = new ArrayList<String>();

    public int startRenaming(File excel, File directory) throws ExcelException, DirectoryException, IOException{
        renamedFiles = new ArrayList<>();
        Map<Integer, String[]> excelData = new HashMap<>();
        try {
            excelData = getExcelData(excel);
        }catch (IOException e){
            throw new ExcelException("Die Excel-Daten konnten nicht gelesen werden.");
        }
        if (validateDirectory(directory) && validateExcel(excelData)){
            rename(directory, excelData);
            if (renamedFiles.size() == 0)
                throw new DirectoryException("Es wurde kein umzubenennendes Bild gefunden.");
            return Arrays.asList(directory.listFiles()).stream().filter(file -> file.getName().matches(makeFileNamePattern(this.fileExtensions))).collect(Collectors.toList()).size() - renamedFiles.size();
        }
        return 0;
    }

    private void rename(File directory, Map<Integer, String[]> excelData){
        File[] directoryListing = directory.listFiles();
        directoryListing = Arrays.asList(directoryListing).stream().sorted(Comparator.comparing(File::getName)).collect(Collectors.toList()).toArray(new File[0]);
        for (int i = 1; i < excelData.size(); i++){
            for (File file : directoryListing) {
                String filename = file.getName();
                if (filename.matches(makeFileNamePattern(this.fileExtensions))) {
                    String ean = excelData.get(i)[0];
                    String matnr = excelData.get(i)[1];
                    if (filename.contains(ean)) {
                        int p = filename.lastIndexOf(".");
                        String filenameExt = filename.substring(p+1);
                        int sCounter = 1;
                        String newFileName = matnr.substring(0, 4) + "_" + matnr.substring(4, 7) + "_" + matnr.substring(7) + "_s" + sCounter + "." + filenameExt;
                        while (new File(directory, newFileName).exists()) {
                            sCounter++;
                            newFileName = matnr.substring(0, 4) + "_" + matnr.substring(4, 7) + "_" + matnr.substring(7) + "_s" + sCounter + "." + filenameExt;
                        }
                        file.renameTo(new File(directory, newFileName));
                        renamedFiles.add(file.getName());
                    }
                }
            }
        }
    }

    private int countMatnrOccurences(List<String> list, String pattern){
        int output = 0;
        for (String listItem : list) {
            if (listItem.matches(pattern))
                output++;
        }
        return output;
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
