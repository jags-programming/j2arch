package com.pjsoft.j2arch.core.model;

import java.util.List;
import java.util.Optional;

public class CodeBase {
    private List<CodeEntity> classes;

    public CodeBase(List<CodeEntity> classes) {
        this.classes = classes;
    }

    public List<CodeEntity> getClasses() {
        return classes;
    }

    public Optional<CodeEntity> findClassByName(String name) {
        return classes.stream().filter(c -> c.getName().equals(name)).findFirst();
    }

    // More helper methods can be added as needed...
}

