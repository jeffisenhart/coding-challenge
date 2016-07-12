package insight.data.science.challenge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Builds the graph of payments as per
 * <code>https://github.com/InsightDataScience/coding-challenge</code>
 * 
 * @author jisenhart
 * @see https://github.com/InsightDataScience/coding-challenge
 */
public class GraphBuilder {
	private Date maxDate;
	private List<Double> medians;// rolling median list
	private List<Payment> payments;
	private Set<Payment> existing;// in case we need to update a paymens date
	private DecimalFormat df = new DecimalFormat("#.00"); 
	public GraphBuilder() {
	}

	private void reset() {
		maxDate = null;
		medians = new ArrayList<Double>();
		payments = new ArrayList<Payment>();
		existing = new HashSet<Payment>();
	}

	public static void main(String[] args) {
		if( args == null || args.length < 2 ){
			System.out.println("Usage: <infile> <outfile>\nSee: https://github.com/InsightDataScience/coding-challenge");
		}else if( !new File(args[0]).exists() ){
			System.out.println("File not found: " + args[0]);
			System.out.println("Usage: <infile> <outfile>\nSee: https://github.com/InsightDataScience/coding-challenge");
		}else{
			if( !new File(args[1]).exists() ){
				try {
					new File(args[1]).createNewFile();
				} catch (IOException e) {
					System.out.println("Output not found and could not be created: " + args[1] + ", error: " + e);
					System.out.println("Usage: <infile> <outfile>\nSee: https://github.com/InsightDataScience/coding-challenge");
					return;
				} 
			}
			new GraphBuilder().build(args[0],args[1]);
		}
	}

	/**
	 * Execution of the build algorithm
	 * 
	 * @param file
	 */
	public void build(String inFile,String outFile) {
		reset();
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(new FileReader(inFile));
			writer = new BufferedWriter(new FileWriter(outFile));
			String line = null;
			ObjectMapper mapper = new ObjectMapper();
			while ((line = reader.readLine()) != null) {
				try {
					Payment p = mapper.readValue(line, Payment.class);
					maybeProcessPament(p);
				} catch (Exception bogusRecord) {
					System.out.println("[record ignored] Could not coerce record to Payment object: " + line + ", actual error: " + bogusRecord);
				}
			}
			for (Double d : medians) {
				writer.write(df.format(d) + "\n");
			}
		} catch (Exception e) {
			System.out.println("Unexpected Exception: " + e);
		} finally {
			try {
				reader.close();
			} catch (Exception e2) {
				// Eat exception...
			}
			try {
				writer.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
	
	/**
	 * 
	 * @param p
	 * @return true if is not null and has all fields present, false otherwise
	 */
	private boolean isValidPayment(Payment p){
		if( p == null ){
			return false;
		}
		if( p.getActor() == null || p.getTarget() == null || p.getCreationTime() == null ){
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @return List of rolling medians - mainly for testing validation
	 */
	public List<Double> getMedians() {
		return medians;
	}
	
	/**
	 * 
	 * @return List of pruned payments - mainly for testing validation
	 */
	public List<Payment> getPayments(){
		return payments;
	}

	/**
	 * Process the payment record (if it is not outside 60 second window)
	 * 
	 * @param file
	 */
	private void maybeProcessPament(Payment p) {
		if(isValidPayment(p)){
			if (!isTooOld(p)) {
				setMaxDate(p);
				if (existing.contains(p)) {
					updateDate(p);
				} else {
					payments.add(p);
				}
				prune();
				calculateMedian();
				existing = new HashSet<Payment>(payments);
			}
		}else{
			System.out.println("[record ignored] Invalid Payment object: " + p.toString() + ", missing actor, target or creation time");
		}
		
	}



	/**
	 * Update the creation time for the payment that maps to <code>p</code> (having same actor and target)
	 * @param p
	 */
	private void updateDate(Payment p) {
		for (Payment payment : payments) {
			if( payment.equals(p) ){
				payment.setCreationTime(p.getCreationTime());
				break;
			}
		}
	}

	/**
	 * Execution of the build algorithm
	 * 
	 * @param file
	 */
	public void build(List<Payment> paymentList) {
		reset();
		for (Payment payment : paymentList) {
			maybeProcessPament(payment);
		}
	}

	/**
	 * Set the current running maxDate
	 * 
	 * @param p
	 */
	private void setMaxDate(Payment p) {
		if (maxDate == null) {
			maxDate = p.getCreationTime();
		} else {
			maxDate = (maxDate.compareTo(p.getCreationTime()) < 0) ? p.getCreationTime() : maxDate;
		}
	}

	/**
	 * Calculate the running median
	 */
	private void calculateMedian() {
		Map<String, Double> counts = new HashMap<String, Double>();
		for (Payment p : payments) {
			if (!counts.containsKey(p.getActor())) {
				counts.put(p.getActor(), 1.00);
			} else {
				Double d = counts.get(p.getActor());
				counts.put(p.getActor(), 1.00 + d);
			}

			if (!counts.containsKey(p.getTarget())) {
				counts.put(p.getTarget(), 1.00);
			} else {
				Double d = counts.get(p.getTarget());
				counts.put(p.getTarget(), 1.00 + d);
			}
		}
		List<Double> list = new ArrayList<Double>(counts.values());
		Collections.sort(list);
		if (list.size() == 1) {
			medians.addAll(list);
		} else if (list.size() % 2 == 0) {
			Double left = list.get((list.size() / 2) - 1);
			Double right = list.get((list.size() / 2));
			medians.add((left + right) / 2);
		} else {
			medians.add(list.get(list.size() / 2));
		}
	}

	/**
	 * Prune any old payments
	 */
	private void prune() {
		Collections.sort(payments, new PaymentComparator());
		int pruneIndex = -1;
		int currentIndex = 0;
		for (Payment p : payments) {
			if (p.getCreationTime().before(maxDate)
					&& getElapsedTimeInSeconds(maxDate.getTime(), p.getCreationTime().getTime()) > 60) {
				pruneIndex = currentIndex;
				currentIndex++;
			} else {
				break;
			}
		}
		if (pruneIndex > -1) {
			while (pruneIndex >= 0) {
				payments.remove(pruneIndex);
				pruneIndex--;
			}
		}
	}

	/**
	 * 
	 * @param p
	 * @return true if the payment is older than the current 60 second window
	 */
	private boolean isTooOld(Payment p) {
		if (maxDate == null) {
			return false;
		}
		if (p.getCreationTime().compareTo(maxDate) >= 0) {
			return false;
		}
		return getElapsedTimeInSeconds(p.getCreationTime().getTime(), maxDate.getTime()) > 60 ? true : false;
	}

	/**
	 * 
	 * @param time1
	 * @param time2
	 * @return the elapsed time in seconds between two longs that represent time
	 *         in milliseconds.
	 */
	private long getElapsedTimeInSeconds(long time1, long time2) {
		return (time1 - time2) / 1000;
	}

}
