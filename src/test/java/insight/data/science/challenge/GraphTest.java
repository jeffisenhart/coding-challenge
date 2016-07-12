package insight.data.science.challenge;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class GraphTest {
	@Test
	  public void testNoPruningSingleEdges(){
	    List<Payment> payments = new ArrayList<Payment>();
	    Calendar cal = Calendar.getInstance();
	    int size = 20;
	    for (int i = 0; i < size; i++) {
	      Payment p = new Payment();
	      p.setActor("Actor" + i);
	      p.setCreationTime(cal.getTime());
	      p.setTarget("Target" + i);
	      cal.add(Calendar.SECOND, 1);
	      payments.add(p);
	    }
	    GraphBuilder builder = new GraphBuilder();
	    builder.build(payments);
	    List<Double> medians = builder.getMedians();
	    for (Double d : medians) {
	      Assert.assertTrue(d.compareTo(1.00) == 0);
	    }
	    Assert.assertTrue(builder.getPayments().size() == size);
	  }
	  
	  @Test
	  public void testNoPruningReplacesDate(){
	    List<Payment> payments = new ArrayList<Payment>();
	    int size = 20;
	    Calendar cal = Calendar.getInstance();
	    for (int i = 0; i < size; i++) {
	      //we will add two payments with the same actor/target date to see if the payments actually updates the date
	      //instead of adding a new payment
	      Payment p = new Payment();
	      p.setActor("Actor" + i);
	      p.setCreationTime(cal.getTime());
	      p.setTarget("Target" + i);
	      cal.add(Calendar.SECOND, 1);
	      payments.add(p);
	      
	      Payment p2 = new Payment();
	      p2.setActor("Actor" + i);
	      p2.setCreationTime(cal.getTime());
	      p2.setTarget("Target" + i);
	      payments.add(p2);
	    }
	    
	    
	    GraphBuilder builder = new GraphBuilder();
	    builder.build(payments);
	    List<Double> medians = builder.getMedians();
	    for (int i = 0; i < medians.size(); i++) {
	    	Assert.assertTrue(medians.get(i).compareTo(1.0) == 0);
		}
	    Assert.assertTrue(builder.getPayments().size() == size);

	  }
	  
	  @Test
	  public void testPruning(){
	    List<Payment> payments = new ArrayList<Payment>();
	    int size = 1;
	    Calendar cal = Calendar.getInstance();
	    for (int i = 0; i < size; i++) {
	      Payment p = new Payment();
	      p.setActor("Actor" + i);
	      p.setCreationTime(cal.getTime());
	      p.setTarget("Target" + i);
	      cal.add(Calendar.SECOND, 1);
	      payments.add(p);
	    }
		cal.add(Calendar.SECOND, 61);
		Payment p = new Payment();
		p.setActor("ActorLast");
		p.setCreationTime(cal.getTime());
		p.setTarget("TargetLast");
		payments.add(p);
	    
	    GraphBuilder builder = new GraphBuilder();
	    builder.build(payments);
	
	    Assert.assertTrue(builder.getMedians().size() == size + 1);
	    Assert.assertTrue(builder.getPayments().size() == 1);
	    Assert.assertTrue(builder.getPayments().get(0).equals(p));
	  }
	  
	  @Test
	  public void testPruning2(){
	    List<Payment> payments = new ArrayList<Payment>();
	    int size = 1;
	    Calendar cal = Calendar.getInstance();
	    for (int i = 0; i < size; i++) {
	      Payment p = new Payment();
	      p.setActor("Actor" + i);
	      p.setCreationTime(cal.getTime());
	      p.setTarget("Target" + i);
	      cal.add(Calendar.SECOND, 61);
	      payments.add(p);
	    }
	    
	    GraphBuilder builder = new GraphBuilder();
	    builder.build(payments);
	
	    Assert.assertTrue(builder.getMedians().size() == size);
	    Assert.assertTrue(builder.getPayments().size() == 1);
	  }
	  
	  @Test
	  public void testNoActor(){
	    List<Payment> payments = new ArrayList<Payment>();
	    Calendar cal = Calendar.getInstance();
		Payment p = new Payment();
		p.setCreationTime(cal.getTime());
		p.setTarget("Target");
		payments.add(p);
	    
	    GraphBuilder builder = new GraphBuilder();
	    builder.build(payments);
	
	    Assert.assertTrue(builder.getMedians().size() == 0);
	    Assert.assertTrue(builder.getPayments().size() == 0);
	  }
	  
	 
	  @Test
	  public void testNoTarget(){
	    List<Payment> payments = new ArrayList<Payment>();
	    Calendar cal = Calendar.getInstance();
		Payment p = new Payment();
		p.setCreationTime(cal.getTime());
		p.setActor("Actor");
		payments.add(p);
	    
	    GraphBuilder builder = new GraphBuilder();
	    builder.build(payments);
	
	    Assert.assertTrue(builder.getMedians().size() == 0);
	    Assert.assertTrue(builder.getPayments().size() == 0);
	  }
	  
	  @Test
	  public void testNoDate(){
	    List<Payment> payments = new ArrayList<Payment>();

		Payment p = new Payment();
		p.setActor("Actor");
		p.setTarget("Target");
		payments.add(p);
	    
	    GraphBuilder builder = new GraphBuilder();
	    builder.build(payments);
	
	    Assert.assertTrue(builder.getMedians().size() == 0);
	    Assert.assertTrue(builder.getPayments().size() == 0);
	  }
	  
	  
}
