package insight.data.science.challenge;

import java.util.Comparator;

/**
 * Sorts <code>Payment</code> by their creationTime (date)
 * @author jisenhart
 *
 */
public class PaymentComparator implements Comparator<Payment> {

  public int compare(Payment o1, Payment o2) {
    return o1.getCreationTime().compareTo(o2.getCreationTime());
  }

}
