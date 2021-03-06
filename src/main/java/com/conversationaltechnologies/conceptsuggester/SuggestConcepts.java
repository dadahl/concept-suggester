package com.conversationaltechnologies.conceptsuggester;

/**
 *
 * @author Deborah Dahl
 */
import static com.conversationaltechnologies.conceptsuggester.SuggestConcepts.utteranceCount;
import gate.Document;
import gate.Corpus;
import gate.CorpusController;
import gate.AnnotationSet;
import gate.Factory;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.persistence.PersistenceManager;
import java.io.BufferedOutputStream;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/*
 *  This application is based on a file originally developed by the 
    University of Sheffield called 'BatchProcessApp.java'
 *
 * Copyright 2019 Conversational Technologies
 * Copyright (c) 2006, The University of Sheffield.
 *
 * The original file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 *
 *  Ian Roberts, March 2006
 *
 *  $Id: BatchProcessApp.java,v 1.5 2006/06/11 19:17:57 ian Exp $
 */
/**
 * This class loads an application from a .gapp file (created using "Save
 * application state" in the GATE GUI), and runs the contained application over
 * one or more files. The results are written out to XML files, either in
 * GateXML format (all annotation sets preserved, as in "save as XML" in the
 * GUI), or with inline XML tags taken from the default annotation set (as in
 * "save preserving format"). In this example, the output file names are simply
 * the input file names with ".out.xml" appended.
 *
 */
public class SuggestConcepts {

    static private Corpus corpus;
    static private CorpusController application;
    private static final String UTTERANCE_PREFIX = "utt";
    static int utteranceCount = 0;
    private static WriteResult resultWriter = new WriteResult();
    private static String directoryToProcess = "." + File.separator + "inputs" + File.separator;

    /**
     * The main entry point. First we parse the command line options (see
     * usage() method for details), then we take all remaining command line
     * parameters to be file names to process. Each file is loaded, processed
     * using the application and the results written to the output file
     * (inputFile.out.xml).
     */
    public static void main(String[] args) throws Exception {
        System.out.println("initializing GATE");
        parseCommandLine(args);
        // initialise GATE - this must be done before calling any GATE APIs
        gate.Gate.init();
        // processFiles("./inputs");
        processCorpus(getDirectoryToProcess());
        //to debug with one file uncomment the next two lines
        //File inputFile = new File("./input.txt");
        //processFile(inputFile);
        System.out.println("All done");
    }

    static void processFiles(String directory) {
        resultWriter.initializeOutputFile();
        try {
            // Create a Corpus to use. The string parameter to newCorpus() is simply the
            // GATE-internal name to use for the corpus.  It has no particular
            // significance.
            setCorpus(Factory.newCorpus("BatchProcessApp Corpus"));
            // load the saved application
            application = (CorpusController) PersistenceManager.loadObjectFromFile(gappFile);
            application.setCorpus(getCorpus());
        } catch (PersistenceException | IOException | ResourceInstantiationException ex) {
            Logger.getLogger(SuggestConcepts.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
            paths.filter(Files::isRegularFile).forEach(file -> processFile(file.toFile()));
        } catch (IOException ex) {
            Logger.getLogger(SuggestConcepts.class.getName()).log(Level.SEVERE, null, ex);
        }
        resultWriter.closeOutputFile();
    }

    static void processFile(File docFile) {
        System.out.print("Processing document " + docFile + "...");
        utteranceCount++;
        String utteranceId = getUTTERANCE_PREFIX() + utteranceCount;
        System.out.println("utterance id: " + utteranceId);
        Document doc;
        try {
            doc = Factory.newDocument(docFile.toURL(), encoding);
            // put the document in the corpus
            getCorpus().add(doc);
            try {
                // run the application
                application.execute();
            } catch (ExecutionException ex) {
                Logger.getLogger(SuggestConcepts.class.getName()).log(Level.SEVERE, null, ex);
            }
            // remove the document from the corpus again
            getCorpus().clear();

            String docXMLString = null;
            // if we want to just write out specific annotation types, we must
            // extract the annotations into a Set
            if (annotTypesToWrite != null) {
                // Create a temporary Set to hold the annotations we wish to write out
                Set annotationsToWrite = new HashSet();

                // we only extract annotations from the default (unnamed) AnnotationSet
                // in this example
                AnnotationSet defaultAnnots = doc.getAnnotations();
                Iterator annotTypesIt = annotTypesToWrite.iterator();
                while (annotTypesIt.hasNext()) {
                    // extract all the annotations of each requested type and add them to
                    // the temporary set
                    AnnotationSet annotsOfThisType
                            = defaultAnnots.get((String) annotTypesIt.next());
                    if (annotsOfThisType != null) {
                        annotationsToWrite.addAll(annotsOfThisType);
                    }
                }
                // create the XML string using these annotations
                docXMLString = doc.toXml(annotationsToWrite);
            } // otherwise, just write out the whole document as GateXML
            else {
                docXMLString = doc.toXml();
            }
            // Release the document, as it is no longer needed
            ExtractConcepts extractConcepts = new ExtractConcepts();
            extractConcepts.getConcepts(utteranceId, doc);
            Factory.deleteResource(doc);
            // output the XML to <inputFile>.out.xml
            // Write output files using the same encoding as the original

            if (isWriteXML()) {
                String outputFileName = "./outputs/" + docFile.getName() + ".out.xml";
                File outputFile = new File(docFile.getParentFile(), outputFileName);
                FileOutputStream fos = new FileOutputStream(outputFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                OutputStreamWriter out;
                if (encoding == null) {
                    out = new OutputStreamWriter(bos);
                } else {
                    out = new OutputStreamWriter(bos, encoding);
                }
                out.write(docXMLString);
                out.close();
                System.out.println("done");
                // for each file
            }
        } catch (Exception ex) {
            Logger.getLogger(SuggestConcepts.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void processCorpus(String directory) {
        resultWriter.initializeOutputFile();
        // load the saved application
        try {
            try {
                // Create a Corpus to use. The string parameter to newCorpus() is simply the
                // GATE-internal name to use for the corpus.  It has no particular
                // significance.
                setCorpus(Factory.newCorpus("BatchProcessApp Corpus"));
            } catch (ResourceInstantiationException ex) {
                Logger.getLogger(SuggestConcepts.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                application = (CorpusController) PersistenceManager.loadObjectFromFile(gappFile);
            } catch (PersistenceException | IOException | ResourceInstantiationException ex) {
                Logger.getLogger(SuggestConcepts.class.getName()).log(Level.SEVERE, null, ex);
            }
            application.setCorpus(getCorpus());
            Stream<Path> paths;
            try {
                paths = Files.walk(Paths.get(directory));
                paths.filter(Files::isRegularFile).forEach(file -> addDocToCorpus(file));
            } catch (IOException ex) {
                Logger.getLogger(SuggestConcepts.class.getName()).log(Level.SEVERE, null, ex);
            }
            // run the application
            application.execute();
            for (Document doc : getCorpus()) {
                utteranceCount++;
                String utteranceId = getUTTERANCE_PREFIX() + utteranceCount;
                ExtractConcepts extractConcepts = new ExtractConcepts();
                extractConcepts.getConcepts(utteranceId, doc);
                if (isWriteXML()) {
                    // output the XML to <inputFile>.out.xml
                    String docXMLString = null;
                    // if we want to just write out specific annotation types, we must
                    // extract the annotations into a Set
                    if (annotTypesToWrite != null) {
                        // Create a temporary Set to hold the annotations we wish to write out
                        Set annotationsToWrite = new HashSet();
                        // we extract annotations from the "Final" AnnotationSet
                        // in this example
                        AnnotationSet defaultAnnots = doc.getAnnotations("Final");
                        Iterator annotTypesIt = annotTypesToWrite.iterator();
                        while (annotTypesIt.hasNext()) {
                            // extract all the annotations of each requested type and add them to
                            // the temporary set
                            AnnotationSet annotsOfThisType
                                    = defaultAnnots.get((String) annotTypesIt.next());
                            if (annotsOfThisType != null) {
                                annotationsToWrite.addAll(annotsOfThisType);
                            }
                        }
                        // create the XML string using these annotations
                        docXMLString = doc.toXml(annotationsToWrite);
                    } // if not interested in specific annotation types, just write 
                    //out the whole document as GateXML
                    else {
                        docXMLString = doc.toXml();
                    }
                    resultWriter.writeXMLFile(encoding, doc, docXMLString);
                    // for each file
                    System.out.println("finished file");
                }
            }
             resultWriter.closeOutputFile();
        } catch (ExecutionException ex) {
            Logger.getLogger(SuggestConcepts.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("finished corpus");
        }
    }

    private static void addDocToCorpus(Path path) {
        utteranceCount++;
        String utteranceId = getUTTERANCE_PREFIX() + utteranceCount;
        System.out.println("utterance id: " + utteranceId);
        try {
            Document doc;
            try {
                doc = Factory.newDocument(path.toFile().toURL(), encoding);
                getCorpus().add(doc);
            } catch (MalformedURLException ex) {
                Logger.getLogger(SuggestConcepts.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ResourceInstantiationException ex) {
            Logger.getLogger(SuggestConcepts.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Parse command line options.
     */
    private static void parseCommandLine(String[] args) throws Exception {
        int i;
        // iterate over all options (arguments starting with '-')
        for (i = 0; i < args.length && args[i].charAt(0) == '-'; i++) {
            switch (args[i].charAt(1)) {
                // -a type = write out annotations of type a.
                case 'a':
                    if (annotTypesToWrite == null) {
                        annotTypesToWrite = new ArrayList();
                    }
                    annotTypesToWrite.add(args[++i]);
                    break;

                // -g gappFile = path to the saved application
                case 'g':
                    gappFile = new File(args[++i]);
                    break;

                // -e encoding = character encoding for documents
                case 'e':
                    encoding = args[++i];
                    break;
                // -x boolean to write xml file
                case 'x':
                    String writeXMLString = args[++i];
                    setWriteXML(Boolean.parseBoolean(writeXMLString));
                    break;
// -d directory to process
                case 'd':
                    String directory = args[++i];
                    setDirectoryToProcess(directory);
                    break;
                default:
                    System.err.println("Unrecognised option " + args[i]);
                    usage();
            }
        }

        // set index of the first non-option argument, which we take as the first
        // file to process
        firstFile = i;

        // sanity check other arguments
        if (gappFile == null) {
            System.err.println("No .gapp file specified");
            usage();
        }
    }

    /**
     * Print a usage message and exit.
     */
    private static final void usage() {
        System.err.println(
                "Usage:\n"
                + "   java sheffield.examples.BatchProcessApp -g <gappFile> [-e encoding]\n"
                + "            [-a annotType] [-a annotType] file1 file2 ... fileN\n"
                + "\n"
                + "-g gappFile : (required) the path to the saved application state we are\n"
                + "              to run over the given documents.  This application must be\n"
                + "              a \"corpus pipeline\" or a \"conditional corpus pipeline\".\n"
                + "\n"
                + "-e encoding : (optional) the character encoding of the source documents.\n"
                + "              If not specified, the platform default encoding (currently\n"
                + "              \"" + System.getProperty("file.encoding") + "\") is assumed.\n"
                + "\n"
                + "-a type     : (optional) write out just the annotations of this type as\n"
                + "              inline XML tags.  Multiple -a options are allowed, and\n"
                + "              annotations of all the specified types will be output.\n"
                + "              This is the equivalent of \"save preserving format\" in the\n"
                + "              GATE GUI.  If no -a option is given the whole of each\n"
                + "              processed document will be output as GateXML (the equivalent\n"
                + "              of \"save as XML\")."
                + "-x boolean   : (optional) write the xml file of the results"
                + "-d directory to process   : (optional) default is './inputs'"
        );

        System.exit(1);
    }

    /**
     * Index of the first non-option argument on the command line.
     */
    private static int firstFile = 0;

    /**
     * Path to the saved application file.
     */
    private static File gappFile = null;

    /**
     * List of annotation types to write out. If null, write everything as
     * GateXML.
     */
    // private static List annotTypesToWrite = null;
    private static List<String> annotTypesToWrite = new ArrayList<>(Arrays.asList("Intent", "Entity", "EntityValue", "EntityValuePair"));
    /**
     * The character encoding to use when loading the documents. If null, the
     * platform default encoding is used.
     */
    private static String encoding = null;

    private static boolean writeXML = false;

    /**
     * @return the corpus
     */
    public static Corpus getCorpus() {
        return corpus;
    }

    /**
     * @param aCorpus the corpus to set
     */
    public static void setCorpus(Corpus aCorpus) {
        corpus = aCorpus;
    }

    /**
     * @return the UTTERANCE_PREFIX
     */
    public static String getUTTERANCE_PREFIX() {
        return UTTERANCE_PREFIX;
    }

    /**
     * @return the resultWriter
     */
    public static WriteResult getResultWriter() {
        return resultWriter;
    }

    /**
     * @param aResultWriter the resultWriter to set
     */
    public static void setResultWriter(WriteResult aResultWriter) {
        resultWriter = aResultWriter;
    }

    /**
     * @return the writeXML
     */
    public static boolean isWriteXML() {
        return writeXML;
    }

    /**
     * @param aWriteXML the writeXML to set
     */
    public static void setWriteXML(boolean aWriteXML) {
        writeXML = aWriteXML;
    }

    /**
     * @return the directoryToProcess
     */
    public static String getDirectoryToProcess() {
        return directoryToProcess;
    }

    /**
     * @param aDirectoryToProcess the directoryToProcess to set
     */
    public static void setDirectoryToProcess(String aDirectoryToProcess) {
        directoryToProcess = aDirectoryToProcess;
    }

}
