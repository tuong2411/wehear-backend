package com.wehear.dto;

public class VslTranslationCorrectionRequest {
    private String sourceText;
    private String modelName;
    private String modelTranslation;
    private String correctedTranslation;

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelTranslation() {
        return modelTranslation;
    }

    public void setModelTranslation(String modelTranslation) {
        this.modelTranslation = modelTranslation;
    }

    public String getCorrectedTranslation() {
        return correctedTranslation;
    }

    public void setCorrectedTranslation(String correctedTranslation) {
        this.correctedTranslation = correctedTranslation;
    }
}
