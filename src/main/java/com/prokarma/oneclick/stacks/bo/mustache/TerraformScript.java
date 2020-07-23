package com.prokarma.oneclick.stacks.bo.mustache;

/**
 * Object matching the terraform mustache template
 */
public class TerraformScript {

    private String terraformImage;
    private String gitRepositoryUrl;
    private String gitDirectory;
    private String gitBranch;
    private String localDirectory;
    private String externalUrl;
    private String stackId;
    private String command;
    private String stateApiUser;
    private String stateApiPassword;
    private String googleCredentials;
    private String googleCredentialsFilePath;
    private String awsAccessKeyId;
    private String awsSecretAccessKey;

    public String getTerraformImage() {
        return terraformImage;
    }

    public TerraformScript setTerraformImage(String terraformImage) {
        this.terraformImage = terraformImage;
        return this;
    }

    public String getGitRepositoryUrl() {
        return gitRepositoryUrl;
    }

    public TerraformScript setGitRepositoryUrl(String gitRepositoryUrl) {
        this.gitRepositoryUrl = gitRepositoryUrl;
        return this;
    }

    public String getGitDirectory() {
        return gitDirectory;
    }

    public TerraformScript setGitDirectory(String gitDirectory) {
        this.gitDirectory = gitDirectory;
        return this;
    }
    
    public String getLocalDirectory() {
        return localDirectory;
    }

    public TerraformScript setLocalDirectory(String localDirectory) {
        this.localDirectory = localDirectory;
        return this;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public TerraformScript setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
        return this;
    }

    public String getStackId() {
        return stackId;
    }

    public TerraformScript setStackId(String stackId) {
        this.stackId = stackId;
        return this;
    }

    public String getCommand() {
        return command;
    }

    public TerraformScript setCommand(String command) {
        this.command = command;
        return this;
    }

    public String getStateApiUser() {
        return stateApiUser;
    }

    public TerraformScript setStateApiUser(String stateApiUser) {
        this.stateApiUser = stateApiUser;
        return this;
    }

    public String getStateApiPassword() {
        return stateApiPassword;
    }

    public TerraformScript setStateApiPassword(String stateApiPassword) {
        this.stateApiPassword = stateApiPassword;
        return this;
    }

    public String getGoogleCredentials() {
        return googleCredentials;
    }

    public void setGoogleCredentials(String googleCredentials) {
        this.googleCredentials = googleCredentials;
    }

    public String getGoogleCredentialsFilePath() {
        return googleCredentialsFilePath;
    }

    public void setGoogleCredentialsFilePath(String googleCredentialsFilePath) {
        this.googleCredentialsFilePath = googleCredentialsFilePath;
    }

    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public String getAwsSecretAccessKey() {
        return awsSecretAccessKey;
    }

    public void setAwsSecretAccessKey(String awsSecretAccessKey) {
        this.awsSecretAccessKey = awsSecretAccessKey;
    }
}
