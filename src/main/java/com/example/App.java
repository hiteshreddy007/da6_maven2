package com.example;

import java.util.ArrayList;
import java.util.List;

public class App {

    public static class InvalidCustomerDataException extends RuntimeException {
        public InvalidCustomerDataException(String message) { super(message); }
    }

    public static class Customer {
        private String id;
        private String name;
        private int age;
        private String govId;
        private double monthlyIncome;
        private int creditScore;
        private double existingMonthlyObligations;

        public Customer(String id, String name, int age, String govId, 
                        double monthlyIncome, int creditScore, double existingMonthlyObligations) {
            if (age < 0) throw new InvalidCustomerDataException("Age cannot be negative.");
            if (monthlyIncome < 0 || existingMonthlyObligations < 0) 
                throw new InvalidCustomerDataException("Financial figures cannot be negative.");
            if (creditScore < 300 || creditScore > 850) 
                throw new InvalidCustomerDataException("Credit score must be between 300 and 850.");
            if (govId == null || govId.isBlank()) 
                throw new InvalidCustomerDataException("Government ID cannot be empty.");
            
            this.id = id;
            this.name = name;
            this.age = age;
            this.govId = govId;
            this.monthlyIncome = monthlyIncome;
            this.creditScore = creditScore;
            this.existingMonthlyObligations = existingMonthlyObligations;
        }

        public String getId() { return id; }
        public int getAge() { return age; }
        public String getGovId() { return govId; }
        public double getMonthlyIncome() { return monthlyIncome; }
        public int getCreditScore() { return creditScore; }
        public double getExistingMonthlyObligations() { return existingMonthlyObligations; }
    }

    public static class LoanApplication {
        private String applicationId;
        private double requestedAmount;

        public LoanApplication(String applicationId, double requestedAmount) {
            if (requestedAmount <= 0) throw new InvalidCustomerDataException("Loan amount must be greater than zero.");
            this.applicationId = applicationId;
            this.requestedAmount = requestedAmount;
        }

        public double getRequestedAmount() { return requestedAmount; }
    }

    public enum RiskClassification { LOW_RISK, MEDIUM_RISK, HIGH_RISK }
    public enum ApprovalStatus { APPROVED, REJECTED }

    public static class CreditAssessment {
        private ApprovalStatus status;
        private RiskClassification risk;
        private double maxPermissibleLoan;
        private List<String> rejectionReasons;

        public CreditAssessment() {
            this.status = ApprovalStatus.APPROVED;
            this.risk = RiskClassification.LOW_RISK;
            this.rejectionReasons = new ArrayList<>();
        }

        public ApprovalStatus getStatus() { return status; }
        public RiskClassification getRisk() { return risk; }
        public double getMaxPermissibleLoan() { return maxPermissibleLoan; }
        public List<String> getRejectionReasons() { return rejectionReasons; }
        
        public void addReason(String reason) { this.rejectionReasons.add(reason); }
    }

    public static CreditAssessment assessLoan(Customer customer, LoanApplication loan) {
        CreditAssessment assessment = new CreditAssessment();

        final int MIN_AGE = 21;
        final double MIN_INCOME = 2000.0;
        final int MIN_CREDIT_SCORE = 600;
        final double MAX_DTI = 0.45; 
        final int EXCELLENT_CREDIT = 750;
        final double LOW_RISK_DTI = 0.20; 

        if (customer.getAge() < MIN_AGE) {
            assessment.addReason("Customer must be at least 21 years old.");
        }

        if (customer.getMonthlyIncome() < MIN_INCOME) {
            assessment.addReason("Income does not meet minimum threshold.");
        }

        if (customer.getCreditScore() < MIN_CREDIT_SCORE) {
            assessment.addReason("Credit score does not meet minimum requirements.");
        }

        double dti = customer.getMonthlyIncome() > 0 ? 
                     (customer.getExistingMonthlyObligations() / customer.getMonthlyIncome()) : 1.0;
        if (dti > MAX_DTI) {
            assessment.addReason("Debt-to-Income ratio exceeds the maximum permissible limit.");
        }

        double incomeMultiplier = customer.getCreditScore() >= EXCELLENT_CREDIT ? 12.0 : 8.0;
        assessment.maxPermissibleLoan = Math.max(0, customer.getMonthlyIncome() * incomeMultiplier);

        if (loan.getRequestedAmount() > assessment.maxPermissibleLoan) {
            assessment.addReason("Requested loan amount exceeds the dynamic income-dependent limit.");
        }

        if (!assessment.getRejectionReasons().isEmpty()) {
            assessment.status = ApprovalStatus.REJECTED;
            assessment.risk = RiskClassification.HIGH_RISK;
        } else {
            if (customer.getCreditScore() >= EXCELLENT_CREDIT && dti <= LOW_RISK_DTI) {
                assessment.risk = RiskClassification.LOW_RISK;
            } else {
                assessment.risk = RiskClassification.MEDIUM_RISK;
            }
        }

        return assessment;
    }
}
