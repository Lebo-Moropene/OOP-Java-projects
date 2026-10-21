package za.co.clemzito.payments;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;



public class PaymentMethodTest {

    @Test
    void shouldHaveDebitCardMethod(){
        assertEquals(
                PaymentMethod.DEBIT_CARD,
                PaymentMethod.valueOf("DEBIT_CARD")
        );
    }

    @Test
    void shouldHaveEftMethods(){
        assertEquals(
                PaymentMethod.EFT,
                PaymentMethod.valueOf("EFT")
        );
    }
}
