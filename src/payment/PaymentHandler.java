package payment;

import company.InsuranceCompany;
import contracts.AbstractContract;
import contracts.InvalidContractException;
import contracts.MasterVehicleContract;


import java.util.*;

public class PaymentHandler {

    private final Map<AbstractContract, Set<PaymentInstance>> paymentHistory;
    private final InsuranceCompany insurer;

    public PaymentHandler(InsuranceCompany insurer) {

        if ( insurer == null ) {
            throw new IllegalArgumentException("insurer is invalid");
        }
        this.insurer = insurer;
        this.paymentHistory = new HashMap<>();
    }

    public Map<AbstractContract,Set<PaymentInstance>> getPaymentHistory() {
        return paymentHistory;
    }

    public void pay(AbstractContract contract, int amount) {

        if ( contract == null || amount <=0 ){
            throw new IllegalArgumentException("contract or amount is invalid");
        }
        if ( !contract.isActive() || this.insurer != contract.getInsurer() ){
            throw new InvalidContractException("contract is invalid");
        }

        contract.getContractPaymentData().setOutstandingBalance(contract.getContractPaymentData().getOutstandingBalance() - amount);
        PaymentInstance paymentInstance = new PaymentInstance(
            insurer.getCurrentTime(),
            amount
        );

        paymentHistory.computeIfAbsent(contract, _ -> new TreeSet<>()).add(paymentInstance);
    }

    public void pay(MasterVehicleContract contract, int amount) {
        if (contract == null || amount <= 0) {
            throw new IllegalArgumentException("contract or amount is invalid");
        }

        int originalAmount = amount;

        if (!contract.isActive() || this.insurer != contract.getInsurer()) {
            throw new InvalidContractException("contract is invalid");
        }
        if (contract.getChildContracts().isEmpty()) {
            throw new InvalidContractException("childContracts are empty");
        }

        for (AbstractContract child : contract.getChildContracts()) {

            if (!child.isActive()) {
                continue;
            }

            ContractPaymentData paymentData = child.getContractPaymentData();
            int balance = paymentData.getOutstandingBalance();

            if (balance > 0) {
                if (amount >= balance) {
                    amount -= balance;
                    paymentData.setOutstandingBalance(0);
                } else {
                    paymentData.setOutstandingBalance(balance - amount);
                    amount = 0;
                    break;
                }
            }
        }

        while( amount > 0 ) {
            boolean paid = false;
            for (AbstractContract child : contract.getChildContracts()){

                if ( !child.isActive() ) {
                    continue;
                }

                ContractPaymentData paymentData = child.getContractPaymentData();
                int premium = paymentData.getPremium();
                if ( amount >= premium ){
                    paymentData.setOutstandingBalance(paymentData.getOutstandingBalance() - premium);
                    amount -= premium;
                    paid = true;
                } else {
                    paymentData.setOutstandingBalance(paymentData.getOutstandingBalance() - amount);
                    amount = 0;
                    paid = true;
                    break;
                }
            }
            if ( !paid ) {
                break;
            }
        }

        PaymentInstance paymentInstance = new PaymentInstance(
                insurer.getCurrentTime(),
                originalAmount
        );
        paymentHistory.computeIfAbsent(contract, _ -> new TreeSet<>()).add(paymentInstance);
    }
    
}
