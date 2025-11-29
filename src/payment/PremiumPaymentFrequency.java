package payment;

public enum PremiumPaymentFrequency {

    ANNUAL, SEMI_ANNUAL, QUARTERLY, MONTHLY;

    public int getValueInMonths() {

        if( this == ANNUAL ) {
            return 12;
        }
        else if( this == SEMI_ANNUAL ) {
            return 6;
        }
        else if ( this == QUARTERLY ) {
            return 3;
        }
        else if ( this == MONTHLY ) {
            return 1;
        }
        else {
            throw new IllegalArgumentException("PremiumPaymentFrequency not recognized");
        }
    }
}
