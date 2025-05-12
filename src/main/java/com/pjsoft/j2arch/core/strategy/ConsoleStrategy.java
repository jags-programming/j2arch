package com.pjsoft.j2arch.core.strategy;

import java.util.List;
import java.util.stream.Collectors;
import com.pjsoft.j2arch.core.model.CodeBase;
import com.pjsoft.j2arch.core.model.CodeEntity;

public class ConsoleStrategy implements EntryPointStrategy {

    @Override
    public List<CodeEntity> identifyEntryPoints(CodeBase codeBase) {
        return codeBase.getClasses().stream()
                .filter(CodeEntity::hasMainMethod) // Check for main method
                .collect(Collectors.toList());
    }

    @Override
    public boolean isApplicableToType(String applicationType) {
        return "CONSOLE".equalsIgnoreCase(applicationType);
    }
}