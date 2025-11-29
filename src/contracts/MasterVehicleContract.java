package contracts;

import company.InsuranceCompany;
import objects.LegalForm;
import objects.Person;

import java.util.LinkedHashSet;
import java.util.Set;

public class MasterVehicleContract extends AbstractVehicleContract {

    private final Set<SingleVehicleContract> childContracts;

    public MasterVehicleContract(String contractNumber, InsuranceCompany insurer,
                                 Person beneficiary, Person policyHolder) {

        super(contractNumber, insurer, beneficiary,
                policyHolder,null,0);

        if( policyHolder.getLegalForm() == LegalForm.NATURAL) {
            throw new IllegalArgumentException("policyHolder can't be NATURAL");

        }

        this.childContracts = new LinkedHashSet<>();

    }

    public Set<SingleVehicleContract> getChildContracts() {
        return childContracts;
    }

    public void requestAdditionOfChildContract(SingleVehicleContract contract) {

        if (contract == null) {
            throw new IllegalArgumentException("contract can't be null");
        }
        if (!contract.getInsurer().equals(this.insurer)) {
            throw new InvalidContractException("contract belongs to another insurer");
        }
        if (!contract.isActive()) {
            throw new InvalidContractException("contract must be active");
        }
        if (childContracts.contains(contract)) {
            throw new InvalidContractException("contract already exists in MasterContract");
        }
        childContracts.add(contract);
    }

    @Override
    public boolean isActive() {

        if(childContracts.isEmpty()) {
            return super.isActive();
        }

        for(SingleVehicleContract c : childContracts) {
            if(c.isActive()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setInactive() {

        for(SingleVehicleContract c : childContracts) {
            c.setInactive();
        }

        super.setInactive();
    }

    @Override
    public void pay(int amount) {

        if( amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }

        this.insurer.getHandler().pay(this,amount);
    }

    @Override
    public void updateBalance() {
        this.insurer.chargePremiumOnContract(this);
    }

}
