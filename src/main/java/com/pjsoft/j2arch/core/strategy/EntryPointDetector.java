package com.pjsoft.j2arch.core.strategy;



import java.util.List;

import com.pjsoft.j2arch.core.model.CodeBase;
import com.pjsoft.j2arch.core.model.CodeEntity;
import com.pjsoft.j2arch.core.strategy.EntryPointStrategy;

public class EntryPointDetector {

    private final List<EntryPointStrategy> strategies;

    public EntryPointDetector(List<EntryPointStrategy> strategies) {
        this.strategies = strategies;
    }

    public void detectAndTagEntryPoints(List<CodeEntity> codeEntities) {
        for (EntryPointStrategy strategy : strategies) {
            for (CodeEntity codeEntity : strategy.identifyEntryPoints(new CodeBase(codeEntities))) {
                codeEntity.setEntryPoint(true);
                codeEntity.setEntryPointType(strategy.getClass().getSimpleName().replace("Strategy", ""));
            }
        }
    }
}
