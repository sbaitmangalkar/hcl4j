package com.baitman.hcl.parser.app;

import com.baitman.hcl.parser.model.ScanDetails;
import com.baitman.hcl.parser.service.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        String defaultIacVersion = "12";
        if(args != null && args.length == 2) {
            ScanDetails scanDetails = new ScanDetails(args[0], defaultIacVersion, args[1]);
            Scanner scanner = Scanner.getScanner();
            scanner.scan(scanDetails);
        }
        System.out.println( "Hello World!" );
    }
}
