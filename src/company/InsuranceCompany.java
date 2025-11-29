package company;

import contracts.*;
import objects.LegalForm;
import objects.Person;
import objects.Vehicle;
import payment.ContractPaymentData;
import payment.PaymentHandler;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

public class InsuranceCompany {

    private final Set<AbstractContract> contracts;
    private final PaymentHandler handler;
    private LocalDateTime currentTime;

    public InsuranceCompany(LocalDateTime currentTime) {

        if(currentTime == null) {
            throw new IllegalArgumentException("currentTime cant be null");
        }

        this.currentTime = currentTime;
        this.contracts = new LinkedHashSet<>();
        this.handler = new PaymentHandler(this);
    }

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(LocalDateTime currentTime) {

        if(currentTime == null) {
            throw new IllegalArgumentException("currentTime cant be null");
        }
        this.currentTime = currentTime;
    }

    public Set<AbstractContract> getContracts() {
        return contracts;
    }

    public PaymentHandler getHandler() {
        return handler;
    }

    public SingleVehicleContract insureVehicle(String contractNumber, Person beneficiary, Person policyHolder,
                                               int proposedPremium, PremiumPaymentFrequency proposedPaymentFrequency,
                                               Vehicle vehicleToInsure) {

        if(contractNumber == null || contractNumber.isEmpty() || policyHolder == null
                || vehicleToInsure == null || proposedPremium <= 0 || proposedPaymentFrequency == null) {
            throw new IllegalArgumentException("invalid input");
        }

        for(AbstractContract c : contracts) {
            if(c.getContractNumber().equals(contractNumber)) {
                throw new IllegalArgumentException("contractNumber already exist");
            }
        }

        int paymentsForYear = 12 / proposedPaymentFrequency.getValueInMonths();
        double yearPremD = proposedPremium * paymentsForYear;
        int yearPrem = (int) Math.floor(yearPremD);
        int requireMin = (int) Math.floor(0.02 * vehicleToInsure.getOriginalValue());

        if (yearPrem < requireMin) {
            throw new IllegalArgumentException("restrictions aren't met for yearPrem");
        }

        ContractPaymentData paymentData = new ContractPaymentData(
                proposedPremium,
                proposedPaymentFrequency,
                currentTime,
                0
        );

        SingleVehicleContract contract = new SingleVehicleContract(
                contractNumber,
                this,
                beneficiary,
                policyHolder,
                paymentData,
                (int) Math.floor(vehicleToInsure.getOriginalValue()/2.0),
                vehicleToInsure
        );

        this.chargePremiumOnContract(contract);
        contracts.add(contract);
        policyHolder.addContract(contract);
        return contract;
    }

    public TravelContract insurePersons(String contractNumber, Person policyHolder, int proposedPremium,
                                        PremiumPaymentFrequency proposedPaymentFrequency,
                                        Set<Person> personsToInsure) {

        if ( contractNumber == null || contractNumber.isEmpty() || policyHolder == null || proposedPremium <= 0 ||
                proposedPaymentFrequency == null || personsToInsure == null || personsToInsure.isEmpty() ){
            throw new IllegalArgumentException("invalid input");
        }

        for ( AbstractContract c : contracts ){
            if ( c.getContractNumber().equals(contractNumber) ) {
                throw new IllegalArgumentException("contractNumber already exist");
            }
        }

        for (Person personToInsure : personsToInsure) {
            if (personToInsure.getLegalForm() != LegalForm.NATURAL) {
                throw new IllegalArgumentException("personToInsure can't be LEGAL");
            }
        }

        int paymentsForYear = 12 / proposedPaymentFrequency.getValueInMonths();
        double yearPremD = proposedPremium * paymentsForYear;
        int yearPrem = (int) Math.floor(yearPremD);

        if ( yearPrem < 5 * personsToInsure.size() ) {
            throw new IllegalArgumentException("restrictions aren't met for yearPrem");
        }

        ContractPaymentData contractPaymentData = new ContractPaymentData(
                proposedPremium,
                proposedPaymentFrequency,
                currentTime,
                0
        );

        TravelContract contract = new TravelContract(
                contractNumber,
                this,
                policyHolder,
                contractPaymentData,
                10 * personsToInsure.size(),
                personsToInsure
        );

        this.chargePremiumOnContract(contract);
        contracts.add(contract);
        policyHolder.addContract(contract);
        return contract;
    }

    public MasterVehicleContract createMasterVehicleContract(String contractNumber, Person beneficiary, Person policyHolder) {

        if ( contractNumber == null || contractNumber.isEmpty() || policyHolder == null
                || policyHolder.getLegalForm() != LegalForm.LEGAL ) {
            throw new IllegalArgumentException("invalid input");
        }
        for (AbstractContract c : contracts) {
            if(c.getContractNumber().equals(contractNumber))
            {
                throw new IllegalArgumentException("contractNumber already exist");
            }
        }

        MasterVehicleContract contract = new MasterVehicleContract(
                contractNumber,
                this,
                beneficiary,
                policyHolder
        );
        contracts.add(contract);
        policyHolder.addContract(contract);
        return contract;
    }

    public void moveSingleVehicleContractToMasterVehicleContract(MasterVehicleContract masterVehicleContract,
                                                                 SingleVehicleContract singleVehicleContract) {

        if(masterVehicleContract == null || singleVehicleContract == null) {
            throw new IllegalArgumentException("contracts can't be null");
        }

        if(!masterVehicleContract.isActive() || !singleVehicleContract.isActive()) {
            throw new InvalidContractException("contracts must be active");
        }

        if(!masterVehicleContract.getInsurer().equals(this) || !singleVehicleContract.getInsurer().equals(this)) {
            throw new InvalidContractException("contracts don't belong to insurer");
        }

        if(!masterVehicleContract.getPolicyHolder().equals(singleVehicleContract.getPolicyHolder())) {
            throw new InvalidContractException("contract don't belong to policyHolder");
        }

        contracts.remove(singleVehicleContract);
        singleVehicleContract.getPolicyHolder().getContracts().remove(singleVehicleContract);
        masterVehicleContract.requestAdditionOfChildContract(singleVehicleContract);
    }

    public void chargePremiumsOnContracts() {

        for(AbstractContract c : contracts) {
            if(c.isActive()) {
                c.updateBalance();
            }
        }
    }

    public void chargePremiumOnContract(AbstractContract contract) {

        ContractPaymentData cpd = contract.getContractPaymentData();

        while(!cpd.getNextPaymentTime().isAfter(currentTime)) {
            cpd.setOutstandingBalance(cpd.getOutstandingBalance() + cpd.getPremium());
            cpd.updateNextPaymentTime();
        }
    }

    public void chargePremiumOnContract(MasterVehicleContract contract) {

        for(SingleVehicleContract c : contract.getChildContracts()) {
            chargePremiumOnContract(c);
        }

    }

    public void processClaim(SingleVehicleContract singleVehicleContract, int expectedDamages) {

        if(singleVehicleContract == null || expectedDamages <= 0) {
            throw new IllegalArgumentException("invalid parameter");
        }

        if (!singleVehicleContract.getInsurer().equals(this)) {
            throw new InvalidContractException("contract belongs to another insurer");
        }

        if(!singleVehicleContract.isActive()) {
            throw new InvalidContractException("contract must be active");
        }

        if(singleVehicleContract.getBeneficiary() != null) {
            singleVehicleContract.getBeneficiary().payout(singleVehicleContract.getCoverageAmount());
        } else {
            singleVehicleContract.getPolicyHolder().payout(singleVehicleContract.getCoverageAmount());
        }

        if(expectedDamages >= (int) Math.floor( 0.7 * singleVehicleContract.getInsuredVehicle().getOriginalValue())) {
            singleVehicleContract.setInactive();
        }

    }

    public void processClaim(TravelContract travelContract, Set<Person> affectedPersons) {

        if(travelContract == null || affectedPersons == null || affectedPersons.isEmpty()) {
            throw new IllegalArgumentException("invalid input");
        }

        if (!travelContract.getInsurer().equals(this)) {
            throw new InvalidContractException("contract belongs to another insurer");
        }

        if(!travelContract.isActive()) {
            throw new InvalidContractException("contract must be active");
        }

        for(Person p : affectedPersons) {
            if(!travelContract.getInsuredPersons().contains(p)) {
                throw new IllegalArgumentException("affectedPersons arent in insuredPersons");
            }
        }

        int insureAmount = affectedPersons.isEmpty() ? 0 : (int) Math.floor((double)travelContract.getCoverageAmount() /(double) affectedPersons.size());

        for(Person p : affectedPersons) {
            p.payout(insureAmount);
        }

        travelContract.setInactive();
    }
}
