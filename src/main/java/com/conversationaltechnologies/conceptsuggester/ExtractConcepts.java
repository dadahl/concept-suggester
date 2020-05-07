/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.conversationaltechnologies.conceptsuggester;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import java.time.LocalDateTime; // import the LocalDate class
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 *
 * @author ddahl
 */
public class ExtractConcepts {

    private String utterance = "";
    private String dateString = "";
    static final String INTENTS_COMMAS = ",";
    static final String COMPOUND_INTENTS_COMMAS = ",,";
    static final String INTENT_CLASS_COMMAS = ",,,";
    static final String BEST_TOPIC_COMMAS = ",,,,";
    static final String BEST_TOPIC_PROB_COMMAS = ",,,,,";
    static final String ENTITIES_COMMAS = ",,,,,,";
    static final String ENTITY_VALUE_COMMAS = ",,,,,,,";
    static final String ENTITY_VALUE_PAIR_COMMAS = ",,,,,,,,";

    public void getConcepts(String utteranceId, Document inputDoc) {
        this.setDateString(LocalDateTime.now().toString());
        String resultString = "";
        //get utterance
        AnnotationSet finalAnnotations = inputDoc.getAnnotations("Final");
        AnnotationSet sentenceAnnotations = finalAnnotations.get("Sentence");
        Annotation utteranceAnnotation = sentenceAnnotations.iterator().next();
        this.setUtterance("\"" + gate.Utils.cleanStringFor(inputDoc, utteranceAnnotation) + "\"");
        String prefixString = utteranceId + ","
                + this.getUtterance() + ","
                + this.getDateString();
        //get intents
        FeatureMap docFeatures = inputDoc.getFeatures();
        ArrayList basicIntents = (ArrayList) docFeatures.get("basicIntents");
        ListIterator<String> listItr = basicIntents.listIterator();
        while (listItr.hasNext()) {
            resultString = resultString + System.lineSeparator() + prefixString + INTENTS_COMMAS + listItr.next();
        }
        //get compound intents
        ArrayList compoundIntents = (ArrayList) docFeatures.get("intents");
        ListIterator<String> listItrIntents = compoundIntents.listIterator();
        while (listItrIntents.hasNext()) {
            resultString = resultString + System.lineSeparator() + prefixString + COMPOUND_INTENTS_COMMAS + listItrIntents.next();
        }
        //get intent, class, confidence triples from topic classification
        AnnotationSet intentClassConfidences = finalAnnotations.get("Intent");
        Iterator intentClassConfidencesIterator = intentClassConfidences.iterator();
        while (intentClassConfidencesIterator.hasNext()) {
            Annotation annotation = (Annotation) intentClassConfidencesIterator.next();
            FeatureMap annotationFeatures = annotation.getFeatures();
            String intent = (String) annotationFeatures.get("intent");
            Integer intentClass = (Integer) annotationFeatures.get("LDA_BestTopic");
            Double intentConfidence = (Double) annotationFeatures.get("LDA_BestTopicProb");
            resultString = resultString + System.lineSeparator() + prefixString + INTENT_CLASS_COMMAS + intent + "," + intentClass + "," + intentConfidence;
        }
        //get entities
        AnnotationSet entities = finalAnnotations.get("Entity");
        Iterator entitiesIterator = entities.iterator();
        while (entitiesIterator.hasNext()) {
            Annotation annotation = (Annotation) entitiesIterator.next();
            FeatureMap annotationFeatures = annotation.getFeatures();
            String root = (String) annotationFeatures.get("root");
            resultString = resultString + System.lineSeparator() + prefixString + ENTITIES_COMMAS + root;
        }
        //get entity values
        AnnotationSet entityValues = finalAnnotations.get("EntityValue");
        Iterator entityValueIterator = entityValues.iterator();
        while (entityValueIterator.hasNext()) {
            Annotation annotation = (Annotation) entityValueIterator.next();
            FeatureMap annotationFeatures = annotation.getFeatures();
            String root = (String) annotationFeatures.get("root");
            resultString = resultString + System.lineSeparator() + prefixString + ENTITY_VALUE_COMMAS + root;
        }
        //get entity value pairs
        AnnotationSet entityValuePair = finalAnnotations.get("EntityValuePair");
        Iterator entityValuesPairIterator = entityValuePair.iterator();
        while (entityValuesPairIterator.hasNext()) {
            Annotation annotation = (Annotation) entityValuesPairIterator.next();
            FeatureMap annotationFeatures = annotation.getFeatures();
            String root = (String) annotationFeatures.get("root");
            String string = (String) annotationFeatures.get("string");
            resultString = resultString + System.lineSeparator() + prefixString + ENTITY_VALUE_PAIR_COMMAS + root + "," + string;
        }
        //resultString = resultString + System.lineSeparator() + prefixString;
        SuggestConcepts.getResultWriter().saveResult(resultString);
    }

    /**
     * @return the utterance
     */
    public String getUtterance() {
        return utterance;
    }

    /**
     * @param utterance the utterance to set
     */
    public void setUtterance(String utterance) {
        this.utterance = utterance;
    }

    /**
     * @return the dateString
     */
    public String getDateString() {
        return dateString;
    }

    /**
     * @param dateString the dateString to set
     */
    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

}
