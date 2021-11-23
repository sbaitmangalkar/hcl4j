package com.baitman.hcl.parser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ScanDetails {
    private String iacType;
    private String iacVersion;
    private String iacFilePath;
}
