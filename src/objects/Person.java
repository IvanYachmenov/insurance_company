package objects;

import contracts.AbstractContract;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

public class Person {

    private final String id;
    private final LegalForm legalForm;
    private int paidOutAmount;
    private final Set<AbstractContract> contracts;

    public Person(String id) {

        if(id == null || id.isEmpty() ) {
            throw new IllegalArgumentException("id can't be null or empty");
        }

        if(isValidRegistrationNumber(id)) {
            this.legalForm = LegalForm.LEGAL;
        }
        else if (isValidBirthNumber(id)) {
            this.legalForm = LegalForm.NATURAL;
        }
        else {
            throw new IllegalArgumentException("id is invalid");
        }

        this.id = id;
        this.paidOutAmount = 0;
        this.contracts = new LinkedHashSet<>();

    }

    public static boolean isValidBirthNumber(String birthNumber) {

        if(birthNumber == null || !(birthNumber.length() == 9 || birthNumber.length() == 10)) {
            return false;
        }

        for(char c : birthNumber.toCharArray()) {
            if(!Character.isDigit(c)) {
                return false;
            }
        }

        int RR = Integer.parseInt(birthNumber.substring(0,2));
        int MM = Integer.parseInt(birthNumber.substring(2,4));
        int DD = Integer.parseInt(birthNumber.substring(4,6));
        
        if(!((MM >= 1 && MM <= 12) || (MM >= 51 && MM <= 62))) {
            return false;
        }
        

        int realMM = ( MM>50 ) ? ( MM-50 ) : MM; //for woman
        int realRR;

        if(birthNumber.length() == 9) {

            if(RR>53) {
                return false;
            }
            realRR = 1900 + RR;

        }
        else {

            realRR = (RR>53) ? (1900 + RR) : (2000 + RR);
            int sum = 0;

            for(int i=0; i < birthNumber.length(); i++) {
                sum += (((int)Math.pow(-1,i))*(birthNumber.charAt(i)-'0'));
            }

            if(sum%11!=0) {
                return false;
            }

        }

        try {
            LocalDate.of(realRR, realMM, DD);
        }
        catch( DateTimeException e ) {
            return false;
        }

        return true;
    }

    public static boolean isValidRegistrationNumber(String registrationNumber)
    {

        if(registrationNumber == null || !(registrationNumber.length() == 6 || registrationNumber.length() == 8)) {
            return false;
        }

        for(char c : registrationNumber.toCharArray()) {
            if(!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }

    public String getId() {
        return id;
    }

    public int getPaidOutAmount() {
        return paidOutAmount;
    }

    public LegalForm getLegalForm() {
        return legalForm;
    }

    public Set<AbstractContract> getContracts() {
        return contracts;
    }

    public void addContract(AbstractContract contract) {

        if(contract == null) {
            throw new IllegalArgumentException();
        }
        contracts.add(contract);
    }

    public void payout(int paidOutAmount) {

        if(paidOutAmount <= 0) {
            throw new IllegalArgumentException();
        }
        this.paidOutAmount += paidOutAmount;
    }

}
