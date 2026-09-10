package com.example;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {

    @Test
    public void testLowRiskApproval() {
        App.Customer customer = new App.Customer("C01", "John", 30, "GOV123", 5000.0, 780, 200.0);
        App.LoanApplication loan = new App.LoanApplication("L01", 20000.0);

        App.CreditAssessment assessment = App.assessLoan(customer, loan);

        assertEquals(App.ApprovalStatus.APPROVED, assessment.getStatus());
        assertEquals(App.RiskClassification.LOW_RISK, assessment.getRisk());
        assertTrue(assessment.getMaxPermissibleLoan() >= loan.getRequestedAmount());
    }

    @Test
    public void testBoundaryConditionsExactMatches() {
        App.Customer customer = new App.Customer("C02", "Alex", 21, "GOV456", 2000.0, 600, 900.0);
        App.LoanApplication loan = new App.LoanApplication("L02", 5000.0);

        App.CreditAssessment assessment = App.assessLoan(customer, loan);

        assertEquals(App.ApprovalStatus.APPROVED, assessment.getStatus());
        assertEquals(App.RiskClassification.MEDIUM_RISK, assessment.getRisk());
        assertEquals(0, assessment.getRejectionReasons().size());
    }

    @Test
    public void testMultipleFailureScenario() {
        App.Customer customer = new App.Customer("C03", "Bob", 19, "GOV789", 3000.0, 500, 100.0);
        App.LoanApplication loan = new App.LoanApplication("L03", 90000.0);

        App.CreditAssessment assessment = App.assessLoan(customer, loan);

        assertEquals(App.ApprovalStatus.REJECTED, assessment.getStatus());
        assertEquals(App.RiskClassification.HIGH_RISK, assessment.getRisk());
        assertTrue(assessment.getRejectionReasons().size() >= 3);
    }

    @Test(expected = App.InvalidCustomerDataException.class)
    public void testInputValidationThrowsCustomException() {
        new App.Customer("C04", "Invalid", 25, "GOV000", 4000.0, 900, 200.0);
    }
}
