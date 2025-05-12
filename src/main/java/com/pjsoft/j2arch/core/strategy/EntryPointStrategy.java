package com.pjsoft.j2arch.core.strategy;

import java.util.List;
import com.pjsoft.j2arch.core.model.CodeBase;
import com.pjsoft.j2arch.core.model.CodeEntity;

public interface EntryPointStrategy {
    /**
     * Runs the strategy to identify entry points in the codebase.
     * @param codeBase The codebase to analyze.
     * @return A list of entry classes/entities identified by this strategy.
     */
    List<CodeEntity> identifyEntryPoints(CodeBase codeBase);

    /**
     * Checks if this strategy is applicable to the given application type.
     * @param applicationType The type of application (e.g., WEB, GUI, CONSOLE).
     * @return True if applicable, false otherwise.
     */
    boolean isApplicableToType(String applicationType);
}