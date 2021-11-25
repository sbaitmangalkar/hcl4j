package com.baitman.hcl.parser.service;

import com.baitman.hcl.parser.model.IaCResource;
import com.baitman.hcl.parser.model.ScanDetails;

import java.util.List;

public interface IaCProvider {
    public abstract List<IaCResource> processIaCDirectory(ScanDetails scanDetails);
}
