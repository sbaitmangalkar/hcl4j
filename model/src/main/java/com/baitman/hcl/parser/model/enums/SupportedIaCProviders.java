package com.baitman.hcl.parser.model.enums;

public enum SupportedIaCProviders {
    TERRAFORM(0, "Terraform");

    private int id;
    private String name;

    SupportedIaCProviders(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static SupportedIaCProviders fromName(String name) {
        for (SupportedIaCProviders value : SupportedIaCProviders.values()) {
            if(value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid supported IaC provider name: " + name);
    }
}
