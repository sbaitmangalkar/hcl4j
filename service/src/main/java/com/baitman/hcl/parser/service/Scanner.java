package com.baitman.hcl.parser.service;

import com.baitman.hcl.parser.model.IaCResource;
import com.baitman.hcl.parser.model.ScanDetails;
import com.baitman.hcl.parser.model.enums.SupportedIaCProviders;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private static volatile Scanner SCANNER;
    private Scanner() {

    }

    public static Scanner getScanner() {
        if(SCANNER == null) {
            synchronized (Scanner.class) {
                if(SCANNER == null) {
                    SCANNER = new Scanner();
                }
            }
        }
        return SCANNER;
    }

    public void scan(ScanDetails scanDetails) {
        IaCProviderFactory factory = IaCProviderFactory.getIaCProviderFactory();
        IaCProvider iaCProvider = factory.getIaCProvider(SupportedIaCProviders.fromName(scanDetails.getIacType()));
        List<IaCResource> resources = iaCProvider.processIaCDirectory(scanDetails);
    }
}
