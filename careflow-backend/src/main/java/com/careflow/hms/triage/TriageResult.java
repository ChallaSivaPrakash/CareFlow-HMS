package com.careflow.hms.triage;

import java.util.ArrayList;
import java.util.List;

public class TriageResult {
    private String triageColor;
    private String rationale;
    private boolean requiresHumanApproval;
    private List<String> warnings = new ArrayList<>();

    public TriageResult() {}

    public TriageResult(String triageColor, String rationale, boolean requiresHumanApproval) {
        this.triageColor = triageColor;
        this.rationale = rationale;
        this.requiresHumanApproval = requiresHumanApproval;
    }

    public String getTriageColor() { return triageColor; }
    public void setTriageColor(String triageColor) { this.triageColor = triageColor; }
    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }
    public boolean isRequiresHumanApproval() { return requiresHumanApproval; }
    public void setRequiresHumanApproval(boolean requiresHumanApproval) { this.requiresHumanApproval = requiresHumanApproval; }
    public List<String> getWarnings() { return warnings; }
    public void addWarning(String warning) { this.warnings.add(warning); }
}
