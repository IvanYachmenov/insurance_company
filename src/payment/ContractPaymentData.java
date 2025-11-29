package payment;

import java.time.LocalDateTime;

public class ContractPaymentData {

    private int premium;
    private PremiumPaymentFrequency premiumPaymentFrequency;
    private LocalDateTime nextPaymentTime;
    private int outstandingBalance;

    public ContractPaymentData(int premium, PremiumPaymentFrequency premiumPaymentFrequency,
                               LocalDateTime nextPaymentTime, int outstandingBalance) {

        if ( premium <= 0 || premiumPaymentFrequency==null || nextPaymentTime == null ){
            throw new IllegalArgumentException("invalid premium or nextPaymentTime or premiumPaymentFrequency");
        }

        this.premium = premium;
        this.premiumPaymentFrequency = premiumPaymentFrequency;
        this.nextPaymentTime = nextPaymentTime;
        this.outstandingBalance = outstandingBalance;
    }

    public int getPremium() {
        return premium;
    }

    public void setPremium(int premium){

        if( premium<=0 ) {
            throw new IllegalArgumentException("premium can't be negative or 0");
        }
        this.premium = premium;
    }

    public void setOutstandingBalance(int outstandingBalance){
        this.outstandingBalance = outstandingBalance;
    }

    public int getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setPremiumPaymentFrequency(PremiumPaymentFrequency premiumPaymentFrequency) {

        if ( premiumPaymentFrequency==null ){
            throw new IllegalArgumentException("premiumPaymentFrequency can't be null");
        }
        this.premiumPaymentFrequency = premiumPaymentFrequency;
    }

    public PremiumPaymentFrequency getPremiumPaymentFrequency() {
        return premiumPaymentFrequency;
    }

    public LocalDateTime getNextPaymentTime() {
        return nextPaymentTime;
    }

    public void updateNextPaymentTime() {
        if ( nextPaymentTime==null ){
            throw new IllegalArgumentException("nextPaymentTime can't be null");
        }
        this.nextPaymentTime = nextPaymentTime.plusMonths(premiumPaymentFrequency.getValueInMonths());
    }
}
