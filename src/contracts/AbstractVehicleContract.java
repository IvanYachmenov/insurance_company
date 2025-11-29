package contracts;

import company.InsuranceCompany;
import objects.Person;
import payment.ContractPaymentData;

import java.util.Objects;

public abstract class AbstractVehicleContract extends AbstractContract {

    protected Person beneficiary;

    public AbstractVehicleContract(String contractNumber,InsuranceCompany insurer,
                                   Person beneficiary, Person policyHolder,
                                   ContractPaymentData contractPaymentData, int coverageAmount) {
        super(contractNumber, insurer, policyHolder, contractPaymentData, coverageAmount);
        if( beneficiary!=null && beneficiary.equals(policyHolder) ) {
            throw new IllegalArgumentException("beneficiary can't be the same as policyHolder");
        }
        this.beneficiary = beneficiary;
    }

    public void setBeneficiary(Person beneficiary) {
        if(beneficiary!=null && beneficiary.equals(policyHolder)) {
            throw new IllegalArgumentException("beneficiary can't be the same as policyHolder");
        }
        this.beneficiary = beneficiary;
    }

    public Person getBeneficiary() {
        return beneficiary;
    }

    @Override
    public boolean equals(Object obj){
        if( this == obj ) return true;
        if( !(obj instanceof AbstractVehicleContract that) ) return false;
        if( !super.equals(obj) ) return false;
        return Objects.equals(beneficiary, that.beneficiary);
    }

    @Override
    public int hashCode(){
        return 31 * super.hashCode() + ( beneficiary != null ? beneficiary.hashCode() : 0 );
    }

}
