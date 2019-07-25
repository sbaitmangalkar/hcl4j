package com.baitman.hcl.parser.service.constants;

public enum TerraformConstants {
    PROVIDER("provider"),
    MODULE("module"),
    DATA("data"),
    VARIABLE("variable"),
    RESOURCE("resource"),
    TAGS("tags"),
    STATEMENT("Statement"),
    LOGGING_BLOCK("logging {"),
    ROOT_BLOCK_DEVICE("root_block_device"),
    INCLUDE_GLOBAL_SERVICE_EVENTS("include_global_service_events"),
    ENABLE_LOGGING("enable_logging"),
    IS_MULTI_REGION_TRAIL("is_multi_region_trail"),
    KMS_KEY_ID("kms_key_id"),
    S3_BUCKET_NAME("s3_bucket_name"),
    NAME("name"),
    CLOUD_WATCH_LOG_GROUP_ARN("cloud_watch_logs_group_arn"),
    ENABLE_LOG_FILE_VALIDATION("enable_log_file_validation"),
    TAGS_BLOCK("tags["),
    CIDR_BLOCK("cidr_block"),
    ENABLE_DNS_SUPPORT("enable_dns_support"),
    DELETION_WINDOW_IN_DAYS("deletion_window_in_days"),
    IS_ENABLED("is_enabled"),
    ENABLE_KEY_ROTATION("enable_key_rotation"),
    KEY_USAGE("key_usage"),
    IAM_INSTANCE_PROFILE("iam_instance_profile"),
    INSTANCE_TYPE("instance_type"),
    SECURITY_GROUPS("security_groups"),
    AMI("ami"),
    SNAPSHOT_ID("snapshot_id"),
    INGRESS("ingress"),
    EGRESS("egress"),
    VPC_ID("vpc_id"),
    LOGGING("logging"),
    VERSIONING_BLOCK("versioning {"),
    LIFECYCLE_RULE("lifecycle_rule"),
    SERVER_SIDE_ENCRYPTION("server_side_encryption_configuration"),
    BUCKET("bucket"),
    POLICY("policy"),
    ACL("acl"),
    VERSION_BLOCK("versioning["),
    MFA_DELETE("mfa_delete"),
    ENABLED("enabled"),
    BLOCK_PUBLIC_ACLS("block_public_acls"),
    BLOCK_PUBLIC_POLICY("block_public_policy"),
    IGNORE_PUBLIC_ACLS("ignore_public_acls"),
    RESTRICT_PUBLIC_BUCKETS("restrict_public_buckets"),
    ACCESS_LOGS_BLOCK("access_logs {"),
    ACCESS_LOGGING_BLOCK("access_logs["),
    INSTANCE_CLASS("instance_class"),
    PUBLICLY_ACCESSIBLE("publicly_accessible"),
    PORT("port"),
    VPC_SECURITY_GROUP_IDS("vpc_security_group_ids"),
    ENGINE("engine"),
    BACKUP_RETENTION_PERIOD("backup_retention_period"),
    STORAGE_ENCRYPTED("storage_encrypted"),
    SOURCE_IDS("source_ids"),
    TYPE("type"),
    ENCRYPTED("encrypted"),
    CLUSTER_IDENTIFIER("cluster_identifier"),
    IP_V6_ENABLED("is_ipv6_enabled"),
    HTTP_VERSION("http_version"),
    ORIGIN("origin");

    private String constantName;

    TerraformConstants(String constantName) {
        this.constantName = constantName;
    }

    public String getConstantName() {
        return constantName;
    }
}
