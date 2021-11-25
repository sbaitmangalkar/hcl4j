package com.baitman.hcl.parser.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baitman.hcl.parser.model.IaCResource;
import com.baitman.hcl.parser.model.ScanDetails;

public class Terraform implements IaCProvider {
    @Override
    public List<IaCResource> processIaCDirectory(ScanDetails scanDetails) {
        if(Files.isDirectory(Paths.get(scanDetails.getIacFilePath()))) {
            // Scan directory recursively
            return loadDirRecursively(scanDetails.getIacFilePath());
        } else {
            // scan file
        }
        return null;
    }

    private List<IaCResource> loadDirRecursively(String dirPath) {
        List<IaCResource> resources  = new ArrayList<>();
        try(Stream<Path> paths = Files.find(Paths.get(dirPath), Integer.MAX_VALUE, (path, attribute) -> attribute.isDirectory())) {
            paths.forEach(path -> {
                if(isIaCConfigDir(path)) {
                    // process TF files
                } else {
                    //TODO: log error message and continue
                }
            });
        } catch (IOException e) {

        }
        return resources;
    }

    private boolean isIaCConfigDir(Path path) {
        try(Stream<Path> pathStream = Files.walk(path)) {
            return pathStream.filter(Files::isRegularFile)
                    .map(filePath -> filePath.toString())
                    .anyMatch(file -> file.endsWith(".tf") || file.endsWith(".tf.json"));
        } catch (IOException e) {
            return false;
        }
    }
}
