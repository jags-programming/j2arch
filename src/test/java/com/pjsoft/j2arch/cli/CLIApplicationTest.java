package com.pjsoft.j2arch.cli;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.pjsoft.j2arch.uml.config.ConfigurationManager;

/**
 * Simplified unit tests for the {@link CLIApplication} class.
 */
class CLIApplicationTest {

    @Test
    void testHandleConfigurationChoice_DefaultSettings() throws Exception {
        // Arrange
        ConfigurationManager configManager = ConfigurationManager.getInstance();
        configManager.clearSettings();

        // Act
        CLIApplication.handleConfigurationChoice("1", configManager);

        // Assert
        assertNotNull(configManager.getProperties(), "Default settings should be loaded");
    }

    @Test
    void testHandleConfigurationChoice_FileBasedSettings() throws Exception {
        // Arrange
        ConfigurationManager configManager = ConfigurationManager.getInstance();
        configManager.clearSettings();

        // Act
        CLIApplication.handleConfigurationChoice("2", configManager);

        // Assert
        assertNotNull(configManager.getProperties(), "File-based settings should be loaded");
    }

}