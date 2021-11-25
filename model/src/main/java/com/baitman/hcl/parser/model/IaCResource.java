package com.baitman.hcl.parser.model;

import com.baitman.hcl.parser.model.enums.IaCResourceType;
import com.baitman.hcl.parser.model.enums.TemplateType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Data
public class IaCResource {
    private String id;
    private String name;
    private long parsedTime;
    private String serviceName;
    private TemplateType templateType;
    private String source;
    private IaCResourceType iaCResourceType;
    private String config;
    private int startLine;
    private int endLine;
    private Map<String, Object> attributes;
    private Map<String, Object> tags;
    private Map<String, Object> additionalDetails;
}
