package contracts;

import company.InsuranceCompany;
import objects.LegalForm;
import objects.Person;
import payment.ContractPaymentData;

import java.util.Set;

public class TravelContract extends AbstractContract {
    private final Set<Person> insuredPersons;

    public TravelContract(String contractNumber, InsuranceCompany insurer,
                          Person policyHolder, ContractPaymentData contractPaymentData,
                          int coverageAmount, Set<Person> personsToInsure) {

        super(contractNumber, insurer, policyHolder, contractPaymentData, coverageAmount);

        if( personsToInsure == null || personsToInsure.isEmpty() )  {
            throw new IllegalArgumentException("personsToInsure can't be null or empty");
        }

        if( contractPaymentData == null ){
            throw new IllegalArgumentException("contractPaymentData can't be null");
        }

        for( Person personToInsure : personsToInsure ) {
            if( personToInsure.getLegalForm() != LegalForm.NATURAL ) {
                throw new IllegalArgumentException("personToInsure can't be LEGAL");
            }
        }
        this.insuredPersons = personsToInsure;
    }

    public Set<Person> getInsuredPersons() {
        return insuredPersons;
    }

}
