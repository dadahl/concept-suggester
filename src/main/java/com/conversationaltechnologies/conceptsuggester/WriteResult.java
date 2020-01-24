/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.conversationaltechnologies.conceptsuggester;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dahl
 */
public class WriteResult {

    private static File outputFile;
    private static final String FILE_NAME = "results.csv";
    private static FileWriter fileWriter;
    static final String HEADER = "id,utterance,time,intent,compound intent,entity,entity value,entity value pair";
    static final String HEADER2 = ",,,,,,,entity,value";

    /*
    
    result for each entity value -- utterance id, utterance, intent, compound intent,entity, entity value and entity value pair
     */
    public void saveResult(String result) {
        try {
            getFileWriter().append(result);
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void writeHeader() {
        try {
            getFileWriter().write(HEADER + System.lineSeparator());
            getFileWriter().write(HEADER2);
            System.out.println("wrote header.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void initializeOutputFile() {
        try {
            setFileWriter(new FileWriter(getFILE_NAME()));
             this.writeHeader();
        } catch (IOException ex) {
            Logger.getLogger(WriteResult.class.getName()).log(Level.SEVERE, null, ex);
        }
        Path outputFilePath = Paths.get(getFILE_NAME());
        if (Files.notExists(outputFilePath)) {
            System.out.println("no file");
            try {
                this.setOutputFile(new File(getFILE_NAME()));
                if (this.getOutputFile().createNewFile()) {
                    System.out.println("File created: " + getFILE_NAME());
                   
                } else {
                    setFileWriter(new FileWriter(getFILE_NAME()));
                    System.out.println("File exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }

    public void closeOutputFile() {
        try {
            getFileWriter().close();
        } catch (IOException ex) {
            Logger.getLogger(WriteResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the outputFile
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * @param outputFile the outputFile to set
     */
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * @return the FILE_NAME
     */
    public static String getFILE_NAME() {
        return FILE_NAME;
    }

    /**
     * @return the fileWriter
     */
    public static FileWriter getFileWriter() {
        return fileWriter;
    }

    /**
     * @param aFileWriter the fileWriter to set
     */
    public static void setFileWriter(FileWriter aFileWriter) {
        fileWriter = aFileWriter;
    }
}
