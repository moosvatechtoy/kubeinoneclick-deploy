package com.prokarma.oneclick.stacks.bo;

import com.prokarma.oneclick.teams.Team;
import com.prokarma.oneclick.teams.User;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a terraform modules instances.
 *
 * It references the module it is based on, with all the values of its variables.
 * It also has a backend configuration, and a provider configuration (in terraform terms).
 */
@MandatoryStackVariablesValidation
@RegexStackVariablesValidation
public class Stack {

    /**
     * This stack's id
     */
    private String id;

    /**
     * The id of the referenced module
     */
    @NotBlank
    private String moduleId;

    /**
     * The variable values of the module
     */
    private Map<String, String> variableValues = new HashMap<>();

    /**
     * The name of the stack
     */
    @NotBlank
    private String name;

    /**
     * The description of the stack
     */
    private String description;

    /**
     * The provider spec
     */
    private String providerSpec;

    private StackState state = StackState.NEW;

    @DBRef
    private Team ownerTeam;

    private BigDecimal estimatedRunningCost;

    @DBRef
    private User createdBy;

    private LocalDateTime createdAt;

    @DBRef
    private User updatedBy;

    private LocalDateTime updatedAt;

    private boolean enableTTL;

    //TTL & Scheduling params
    private boolean deploySchedule;
    private String deployScheduleExpression;
    private boolean destroySchedule;
    private String destroyScheduleExpression;
    private String destroyType;
    private String destroyAfterHours;
    private String destroyAfterDate;
    private String destroyAfterTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public Map<String, String> getVariableValues() {
        return variableValues;
    }

    public void setVariableValues(Map<String, String> variableValues) {
        this.variableValues = variableValues;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProviderSpec() {
        return providerSpec;
    }

    public void setProviderSpec(String providerSpec) {
        this.providerSpec = providerSpec;
    }

    public StackState getState() {
        return state;
    }

    public void setState(StackState state) {
        this.state = state;
    }

    public void setOwnerTeam(Team ownerTeam) {
        this.ownerTeam = ownerTeam;
    }

    public Team getOwnerTeam() {
        return ownerTeam;
    }

    public BigDecimal getEstimatedRunningCost() {
        return estimatedRunningCost;
    }

    public void setEstimatedRunningCost(BigDecimal estimatedRunningCost) {
        this.estimatedRunningCost = estimatedRunningCost;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isEnableTTL() {
        return enableTTL;
    }

    public void setEnableTTL(boolean enableTTL) {
        this.enableTTL = enableTTL;
    }

    public boolean isDeploySchedule() {
        return deploySchedule;
    }

    public void setDeploySchedule(boolean deploySchedule) {
        this.deploySchedule = deploySchedule;
    }

    public String getDeployScheduleExpression() {
        return deployScheduleExpression;
    }

    public void setDeployScheduleExpression(String deployScheduleExpression) {
        this.deployScheduleExpression = deployScheduleExpression;
    }

    public boolean isDestroySchedule() {
        return destroySchedule;
    }

    public void setDestroySchedule(boolean destroySchedule) {
        this.destroySchedule = destroySchedule;
    }

    public String getDestroyScheduleExpression() {
        return destroyScheduleExpression;
    }

    public void setDestroyScheduleExpression(String destroyScheduleExpression) {
        this.destroyScheduleExpression = destroyScheduleExpression;
    }

    public String getDestroyType() {
        return destroyType;
    }

    public void setDestroyType(String destroyType) {
        this.destroyType = destroyType;
    }

    public String getDestroyAfterHours() {
        return destroyAfterHours;
    }

    public void setDestroyAfterHours(String destroyAfterHours) {
        this.destroyAfterHours = destroyAfterHours;
    }

    public String getDestroyAfterDate() {
        return destroyAfterDate;
    }

    public void setDestroyAfterDate(String destroyAfterDate) {
        this.destroyAfterDate = destroyAfterDate;
    }

    public String getDestroyAfterTime() {
        return destroyAfterTime;
    }

    public void setDestroyAfterTime(String destroyAfterTime) {
        this.destroyAfterTime = destroyAfterTime;
    }
}