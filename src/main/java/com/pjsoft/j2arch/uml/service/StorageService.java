package com.pjsoft.j2arch.uml.service;

import java.util.*;

import com.pjsoft.j2arch.uml.config.ConfigurationManager;

/**
 * Storage Service
 * 
 * Manages storing UML diagrams.
 */
public class StorageService {
    private ConfigurationManager config;

    public StorageService(ConfigurationManager config) {
        this.config = config;
    }

    public void storeDiagram(String format, String outputPath) {
        // Store logic
    }
}