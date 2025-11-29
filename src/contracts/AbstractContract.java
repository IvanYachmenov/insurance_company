package contracts;

import company.InsuranceCompany;
import objects.Person;
import payment.ContractPaymentData;

import java.util.Objects;

public abstract class AbstractContract {

    private final String contractNumber;
    protected final InsuranceCompany insurer;
    protected final Person policyHolder;
    protected final ContractPaymentData contractPaymentData;
    protected int coverageAmount;
    protected boolean isActive;

    public AbstractContract(String contractNumber, InsuranceCompany insurer,
                Person policyHolder, ContractPaymentData contractPaymentData,
                int coverageAmount) {

        if( contractNumber == null || contractNumber.isEmpty() ) {
            throw new IllegalArgumentException("contractNumber can't be null or empty");
        }
        if( insurer == null ) {
            throw new IllegalArgumentException("insurer cant be null");
        }

        if( policyHolder == null ) {
            throw new IllegalArgumentException("policyHolder can't be null");
        }
        if( coverageAmount < 0 ) {
            throw new IllegalArgumentException("coverageAmount can't be < 0");
        }

        this.contractNumber = contractNumber;
        this.insurer = insurer;
        this.policyHolder = policyHolder;
        this.contractPaymentData = contractPaymentData;
        this.coverageAmount = coverageAmount;
        this.isActive = true;

    }

    public String getContractNumber() {
        return contractNumber;
    }

    public Person getPolicyHolder() {
        return policyHolder;
    }

    public InsuranceCompany getInsurer() {
        return insurer;
    }

    public int getCoverageAmount() {
        return coverageAmount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setInactive() {
        isActive = false;
    }

    public void setCoverageAmount(int coverageAmount) {

        if( coverageAmount < 0 ) {
            throw new IllegalArgumentException("coverageAmount can't be < 0");
        }
        this.coverageAmount = coverageAmount;

    }

    public ContractPaymentData getContractPaymentData() {
        return contractPaymentData;
    }

    public void pay(int amount) {
        if( amount <= 0 ) {
            throw new IllegalArgumentException("amount must be >0");
        }
        insurer.getHandler().pay(this,amount);
    }

    public void updateBalance() {
        insurer.chargePremiumOnContract(this);
    }

    @Override
    public boolean equals(Object obj) {
        if( this == obj ) return true;
        if( !(obj instanceof AbstractContract other) ) return false;
        return contractNumber.equals(other.contractNumber)
                && insurer.equals(other.insurer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractNumber, insurer);
    }
}
