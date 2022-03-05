package com.ifpb.experiment.actions;

import com.intellij.openapi.project.Project;

public interface DataExtractor {
    int samplesCount = 32;

    Project getProject();
    void extractOf(Project projeto);
    void extractOfAll(Project projeto);
    void extractOf(String Abspath);
    void extractOf();
    void extractOfAll();
}
