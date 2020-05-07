/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.conversationaltechnologies.conceptsuggester;

import gate.Document;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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
    static final String HEADER = "id,utterance,time,intent,"
            + "compound intent,intentClass,bestTopicConfidence,bestTopicProb,entity,"
            + "entity value,entity value pair";
    static final String HEADER2 = ",,,,,,,,,,entity,value";
    private final String XML_DIR = "."
            + File.separator
            + "outputs"
            + File.separator;
    private static final String XML_FILE_SUFFIX = "out.xml";

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

    public void writeXMLFile(String encoding, Document doc, String docXMLString) {
        File docDir = new File(getXML_DIR());
        boolean dirCreated = docDir.mkdirs();
        if (docDir.exists()) {
            String outputFileName = getXML_DIR() + doc.getName() + getXML_FILE_SUFFIX();
            File docFile = new File(outputFileName);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(docFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                OutputStreamWriter out;
                if (encoding == null) {
                    out = new OutputStreamWriter(bos);
                } else {
                    out = new OutputStreamWriter(bos, encoding);
                }
                out.write(docXMLString);
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(WriteResult.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    /**
     * @return the XML_DIR
     */
    public String getXML_DIR() {
        return XML_DIR;
    }

    /**
     * @return the XML_FILE_SUFFIX
     */
    public static String getXML_FILE_SUFFIX() {
        return XML_FILE_SUFFIX;
    }
}
