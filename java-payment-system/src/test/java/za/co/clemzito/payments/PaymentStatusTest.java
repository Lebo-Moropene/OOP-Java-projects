package za.co.clemzito.payments;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentStatusTest {

    @Test
    void shouldContainAllPaymentStauses(){
        assertEquals(
                3,
                PaymentStatus.values().length
        );
    }



    @Test
    void shouldHaveSuccessfulStatus() {
        assertEquals(
                PaymentStatus.SUCCESSFUL,
                PaymentStatus.valueOf("SUCCESSFUL")
        );
    }
    @Test
    void shouldHaveFailedStatus() {
        assertEquals(
                PaymentStatus.FAILED,
                PaymentStatus.valueOf("FAILED")
        );
    }

}

