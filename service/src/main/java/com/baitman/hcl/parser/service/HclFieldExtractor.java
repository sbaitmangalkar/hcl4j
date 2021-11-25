package com.baitman.hcl.parser.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import com.baitman.hcl.parser.service.exception.HclException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.baitman.hcl.parser.service.constants.TerraformConstants;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class HclFieldExtractor {
    // used to break HCL blocks in terraform template file
    private static final String BLOCK_DIVIDER_REGEX = "(\\n)+(?=[a-zA-Z])+";

    public List<Map<String, List<String>>> extractHclData(InputStream terraformFileInputStream,
        String encoding) {
        try {
            return extractHclData(IOUtils.toString(terraformFileInputStream, encoding));
        } catch (IOException e) {
            throw new HclException(
                "Couldn't extract HCL field information from the given input stream");
        }
    }

    /**
     * @param terraformFile
     * @return
     * @throws IOException
     */
    public List<Map<String, List<String>>> extractHclData(File terraformFile, String encoding) {
        try {
            String fileContent = FileUtils.readFileToString(terraformFile, encoding);
            return extractHclData(fileContent);
        } catch (IOException e) {
            throw new HclException("Couldn't extract HCL field information from the given file");
        }
    }

    /**
     * Extracts HCL fields from the given file content
     *
     * @param fileContent
     * @return
     */
    public List<Map<String, List<String>>> extractHclData(String fileContent) {
        List<Map<String, List<String>>> fileData = new ArrayList<>();
        try {
            String[] fileBlocks = fileContent.split(BLOCK_DIVIDER_REGEX);
            if (fileBlocks != null && fileBlocks.length > 0) {
                for (String eachBlock : fileBlocks) {
                    //For every block extract provider, module, variable, data and resource info
                    if (eachBlock.startsWith(TerraformConstants.PROVIDER.getConstantName())) {
                        Map<String, List<String>> providerMap =
                            extractProviderOrVariableInfo(eachBlock,
                                TerraformConstants.PROVIDER.getConstantName());
                        fileData.add(providerMap);
                    } else if (eachBlock.startsWith(TerraformConstants.MODULE.getConstantName())) {
                        Map<String, List<String>> moduleMap = extractBlockInfo(eachBlock,
                            TerraformConstants.MODULE.getConstantName());
                        fileData.add(moduleMap);
                    } else if (eachBlock.startsWith(TerraformConstants.DATA.getConstantName())) {
                        Map<String, List<String>> dataMap =
                            extractBlockInfo(eachBlock, TerraformConstants.DATA.getConstantName());
                        fileData.add(dataMap);
                    } else if (eachBlock
                        .startsWith(TerraformConstants.VARIABLE.getConstantName())) {
                        Map<String, List<String>> variableMap =
                            extractProviderOrVariableInfo(eachBlock,
                                TerraformConstants.VARIABLE.getConstantName());
                        fileData.add(variableMap);
                    } else if (eachBlock
                        .startsWith(TerraformConstants.RESOURCE.getConstantName())) {
                        Map<String, List<String>> resourceMap = extractBlockInfo(eachBlock,
                            TerraformConstants.RESOURCE.getConstantName());
                        fileData.add(resourceMap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileData;
    }

    /**
     * Extracts the information from HCL blocks
     *
     * @param eachBlock
     * @return
     */
    private Map<String, List<String>> extractBlockInfo(String eachBlock, String blockType) {
        Map<String, List<String>> blockDetails = new HashMap<>();
        String blockName = StringUtils.substringBetween(eachBlock, blockType, "{");
        blockName = blockName.trim();
        blockName = blockName.replaceAll("\"", "");
        blockName = blockType + " " + blockName;
        blockName = Joiner.on(".").join(blockName.split("\\s"));
        List<String> value = new ArrayList<>();

        // Extract tags block if present
        if (eachBlock.contains(TerraformConstants.TAGS.getConstantName())) {
            String tags = StringUtils
                .substringBetween(eachBlock, TerraformConstants.TAGS.getConstantName(), "}");
            List<String> tagsList =
                extractDataFromHclObject(tags, TerraformConstants.TAGS.getConstantName());
            value.addAll(tagsList);
        }

        if (eachBlock.contains(TerraformConstants.ROOT_BLOCK_DEVICE.getConstantName())) {
            String rootBlockDevices = StringUtils
                .substringBetween(eachBlock, TerraformConstants.ROOT_BLOCK_DEVICE.getConstantName(),
                    "]");
            List<String> rootBlockDevicesList = extractDataFromHclArray(rootBlockDevices,
                TerraformConstants.ROOT_BLOCK_DEVICE.getConstantName());
            value.addAll(rootBlockDevicesList);
        }

        // Extract INGRESS data if present
        if (eachBlock.contains(TerraformConstants.INGRESS.getConstantName())) {
            String[] ingresses = eachBlock.split(TerraformConstants.INGRESS.getConstantName());
            List<String> ingressData =
                Arrays.stream(ingresses).map(str -> str.trim()).filter(str -> str.startsWith("{"))
                    .map(str -> StringUtils.substringBetween(str, "{", "}"))
                    .map(str -> str.replaceAll("\\n", ",")).filter(str -> str.startsWith(","))
                    .map(str -> str.replaceFirst(",", TerraformConstants.INGRESS.getConstantName()))
                    .collect(Collectors.toList());
            value.addAll(ingressData);
        }

        //Extract EGRESS data if present
        if (eachBlock.contains(TerraformConstants.EGRESS.getConstantName())) {
            String[] ingresses = eachBlock.split(TerraformConstants.EGRESS.getConstantName());
            List<String> egressData =
                Arrays.stream(ingresses).map(str -> str.trim()).filter(str -> str.startsWith("{"))
                    .map(str -> StringUtils.substringBetween(str, "{", "}"))
                    .map(str -> str.replaceAll("\\n", ",")).filter(str -> str.startsWith(","))
                    .map(str -> str.replaceFirst(",", TerraformConstants.EGRESS.getConstantName()))
                    .collect(Collectors.toList());
            value.addAll(egressData);
        }

        //Extract lifecycle rules if present
        if (eachBlock.contains(TerraformConstants.LIFECYCLE_RULE.getConstantName())) {
            String[] lifecycleRules =
                eachBlock.split(TerraformConstants.LIFECYCLE_RULE.getConstantName());
            List<String> lifeCycleRuleList = Arrays.stream(lifecycleRules).map(str -> str.trim())
                .filter(str -> str.startsWith("{"))
                .map(str -> StringUtils.substringBeforeLast(str, "}"))
                .map(str -> str.replaceAll("\\n", ","))
                .map(str -> TerraformConstants.LIFECYCLE_RULE.getConstantName() + "." + str)
                .collect(Collectors.toList());
            value.addAll(lifeCycleRuleList);
        }

        if (eachBlock.contains(TerraformConstants.SERVER_SIDE_ENCRYPTION.getConstantName())) {
            value.addAll(
                Lists.newArrayList(TerraformConstants.SERVER_SIDE_ENCRYPTION.getConstantName()));
        }

        // Extract origin if present
        if (eachBlock.contains(TerraformConstants.ORIGIN.getConstantName())) {
            String[] origins = eachBlock.split("(?=origin \\{)");
            List<String> originsList = Arrays.stream(origins).map(str -> str.trim())
                .filter(str -> str.startsWith(TerraformConstants.ORIGIN.getConstantName()))
                .map(str -> StringUtils.substringBefore(str, "}"))
                .map(str -> str.replaceAll("\\n", ",")).collect(Collectors.toList());
            value.addAll(originsList);
        }

        //Extract access logs if present
        if (eachBlock.contains(TerraformConstants.ACCESS_LOGS_BLOCK.getConstantName())) {
            String accessLogs =
                TerraformConstants.ACCESS_LOGS_BLOCK.getConstantName().replaceFirst("\\{", "")
                    .trim();
            String logging = StringUtils.substringBetween(eachBlock, accessLogs, "}");
            List<String> loggingList = extractDataFromHclObject(logging, accessLogs);
            value.addAll(loggingList);
        }

        /*
         * Terraform template can also contain Json. If Json is present within terraform template, then
         * in order to distinguish between native terraform and Json, "<<" is used as a separator.
         *
         * Example: If bucket policy in AWS S3 terraform template needs to added as a direct Json
         * string, the the syntax would look as shown below:
         *
         * policy = <<POLICY
         *
         * { "Version": "2012-10-17", "Statement": [ { "Sid": "AWSCloudTrailAclCheck", "Effect":
         * "Allow", "Principal": { "Service": "cloudtrail.amazonaws.com" }, "Action": "s3:GetBucketAcl",
         * "Resource": "arn:aws:s3:::tf-test-trail" } ] }
         *
         * POLICY
         *
         *
         */
        if (eachBlock.contains("<<")) {
            String customBlockName = StringUtils.substringBetween(eachBlock, "<<", "{");
            String customBlock = StringUtils.substringAfter(eachBlock, customBlockName);
            value.addAll(Lists.newArrayList("customBlock_" + customBlock));
        }

        // Extract statement block if present
        if (eachBlock.contains(TerraformConstants.STATEMENT.getConstantName())) {
            String statements = StringUtils
                .substringBetween(eachBlock, TerraformConstants.STATEMENT.getConstantName(), "]");
            List<String> statementList =
                extractDataFromHclArray(statements, TerraformConstants.STATEMENT.getConstantName());
            value.addAll(statementList);
        }
        // Extract logging block if present
        if (eachBlock.contains(TerraformConstants.LOGGING_BLOCK.getConstantName())) {
            String loggingType =
                TerraformConstants.LOGGING_BLOCK.getConstantName().replaceFirst("\\{", "").trim();
            String logging = StringUtils.substringBetween(eachBlock, loggingType, "}");
            List<String> loggingList = extractDataFromHclObject(logging, loggingType);
            value.addAll(loggingList);
        }

        // Extract versioning block if present
        if (eachBlock.contains(TerraformConstants.VERSIONING_BLOCK.getConstantName())) {
            String type =
                TerraformConstants.VERSIONING_BLOCK.getConstantName().replaceFirst("\\{", "")
                    .trim();
            String data = StringUtils.substringBetween(eachBlock, type, "}");
            List<String> result = extractDataFromHclObject(data, type);
            value.addAll(result);
        }

        String[] blockInfos = eachBlock.split("\\n");
        if (blockInfos != null && blockInfos.length > 0) {
            // Skipping the first line since it will be field name.
            for (int beginIndex = 1; beginIndex < blockInfos.length; beginIndex++) {
                if (blockInfos[beginIndex].isEmpty()) {
                    continue;
                }
                if (blockInfos[beginIndex].contains("=") || blockInfos[beginIndex].trim()
                    .startsWith("\"$")) {
                    value.add(blockInfos[beginIndex].trim());
                }
            }
        }
        blockDetails.put(blockName, value);
        return blockDetails;
    }

    /**
     * Extracts provider and variable information from the
     * given HCL block
     *
     * @param eachBlock
     * @param blockType
     * @return
     */
    private Map<String, List<String>> extractProviderOrVariableInfo(String eachBlock,
        String blockType) {
        Map<String, List<String>> providerDetails = new HashMap<>();
        String name = StringUtils.substringBetween(eachBlock, blockType, "{");
        name = name.trim();
        name = name.replaceAll("\"", "");
        name = blockType + "." + name;
        String[] blockInfos = eachBlock.split("\\n");
        List<String> values = new ArrayList<>();
        if (blockInfos != null && blockInfos.length > 0) {
            for (int beginIndex = 1; beginIndex < blockInfos.length; beginIndex++) {
                if (blockInfos[beginIndex].contains("=")) {
                    values.add(blockInfos[beginIndex].trim());
                }
            }
        }
        providerDetails.put(name, values);
        return providerDetails;
    }

    /**
     * Extracts information from given HCL array
     *
     * @param hclArrayString
     * @param arrayType
     * @return
     */
    private List<String> extractDataFromHclArray(String hclArrayString, String arrayType) {
        String arrayContentString = StringUtils.substringAfter(hclArrayString, "[");
        String[] arrayContents = arrayContentString.split(",");
        List<String> result = new ArrayList<>();
        if (arrayContents != null && arrayContents.length > 0) {
            int index = 0;
            for (String eachArrayContent : arrayContents) {
                StringBuilder prefixBuilder = new StringBuilder();
                prefixBuilder.append(arrayType).append("[").append(index).append("]").append(".");
                String[] contents = eachArrayContent.split("\\n");
                for (int begin = 1; begin < contents.length; begin++) {
                    if (contents[begin].contains("=") || contents[begin].contains(":")) {
                        contents[begin] = contents[begin].replaceFirst(":", "=");
                        String value = prefixBuilder.toString() + contents[begin].trim();
                        result.add(value);
                    }
                }
                index++;
            }
        }
        return result;
    }

    /**
     * Extracts information from given HCl object
     *
     * @param hclObjectString
     * @param objectType
     * @return
     */
    private List<String> extractDataFromHclObject(String hclObjectString, String objectType) {
        String objectString = StringUtils.substringAfter(hclObjectString, "{");
        String[] objectContents = objectString.split("\\n");
        List<String> result = new ArrayList<>();
        if (objectContents != null && objectContents.length > 0) {
            for (int begin = 1; begin < objectContents.length; begin++) {
                if (objectContents[begin].contains("=") || objectContents[begin].contains(":")) {
                    StringBuilder objectContentBuilder = new StringBuilder();
                    objectContentBuilder.append(objectType).append("[").append(begin).append("]")
                        .append(".").append(objectContents[begin].trim());
                    result.add(objectContentBuilder.toString());
                }
            }
        }
        return result;
    }
}
