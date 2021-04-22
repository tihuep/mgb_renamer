package ch.mgb.younique.renamer.model;

import java.io.File;

public class RenamerModel {
    public String errorMessage = "";

    public void startRenaming(File excel, File directory){
        if (validateExcel(excel)) {
            //TODO
            //do ya thing
        }
    }

    public boolean validateExcel(File excel){
        //TODO
        return true;
    }
}
