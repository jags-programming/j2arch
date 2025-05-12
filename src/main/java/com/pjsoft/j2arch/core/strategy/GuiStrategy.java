package com.pjsoft.j2arch.core.strategy;

import java.util.List;
import java.util.stream.Collectors;
import com.pjsoft.j2arch.core.model.CodeBase;
import com.pjsoft.j2arch.core.model.CodeEntity;

public class GuiStrategy implements EntryPointStrategy {


    @Override
public List<CodeEntity> identifyEntryPoints(CodeBase codeBase) {
    return codeBase.getClasses().stream()
            .filter(c -> c.hasAnyAnnotation(List.of("FXML", "Component", "UI")) // GUI annotations
                    || c.getMethods().stream()
                        .anyMatch(m -> m.hasParameterType("ActionEvent") || m.getName().startsWith("setOn")) // Event handlers
                    || c.getName().toLowerCase().contains("tab") // Class name patterns
                    || c.getName().toLowerCase().contains("window"))
            .collect(Collectors.toList());
}
    @Override
    public boolean isApplicableToType(String applicationType) {
        return "GUI".equalsIgnoreCase(applicationType);
    }
}