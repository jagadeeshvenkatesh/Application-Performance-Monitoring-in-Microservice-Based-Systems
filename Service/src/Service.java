import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service {
	private static Logger logger = LoggerFactory.getLogger(Service.class);
	
	public static void main(String[] args) throws Exception {
	
        logger.info("Service started");
     
        // Set time for timestamps
		String currentTime = String.valueOf(System.currentTimeMillis());
		setTimestampForDataProcessing(currentTime);
		
		// SnapshotIds of components for the sock-shop application and the underlying CASPA environment
		ArrayList<String> hostlist = new ArrayList<>();
		hostlist.add("R6x_nuhhLZq1wrr9X7aVH1N0nCs");
		hostlist.add("N0n3QwIlX358zNC_Xp8QKW0NSnY");
		
		ArrayList<String> k8s_vs_2_novalocal_Containerlist = new ArrayList<>();
		k8s_vs_2_novalocal_Containerlist.add("fjvVljmb3WtK1C1HA3oojODIn8I");
		k8s_vs_2_novalocal_Containerlist.add("bwal-kQILsT3lXZjCf7sWVed3NI");
		k8s_vs_2_novalocal_Containerlist.add("7uHhWaExHla_-v2wFtzkS5CT-TA");
		k8s_vs_2_novalocal_Containerlist.add("pRfb52KLz9w6foa6hSfY3UoZK3A");
		k8s_vs_2_novalocal_Containerlist.add("-pe7M16LPFZkCn1l8gUsPh2JabM");
		k8s_vs_2_novalocal_Containerlist.add("LuHyNPLMtaHGbSa3FwPjfNPoIxg");
		k8s_vs_2_novalocal_Containerlist.add("ssRD2JoFViN3DTLLQs18daO0bCk");
		k8s_vs_2_novalocal_Containerlist.add("coLNMy43ixx0RF92aRFKGTtOQC8");
			
		ArrayList<String> servicelist = new ArrayList<>();
		servicelist.add("b9xeCfs_8x6YIqZ06Rv3bzxpOHA");
		servicelist.add("sHN0tA3GC3NXC926h3oNBxq8ROA");
		
		ArrayList<String> k8s_vs_3_novalocal_Containerlist = new ArrayList<>();
		k8s_vs_3_novalocal_Containerlist.add("XdZftrQ-kMpourirKbeEF0g0Sqw");
		k8s_vs_3_novalocal_Containerlist.add("73yPaqcB1vgPPcRyV9Mw79KxahU");
		k8s_vs_3_novalocal_Containerlist.add("Y6mCmPxSVaqJ8gRgFAJjQP9uj_4");
		k8s_vs_3_novalocal_Containerlist.add("KXxcRow3mOUOjHbzO1l4IFI3Zq8");
		k8s_vs_3_novalocal_Containerlist.add("ZKdcVJ6Io9m8bykScofxAJCb9hQ");
		k8s_vs_3_novalocal_Containerlist.add("WjCGPmWc5dw_1yseTCz_q6GFVeo");
		k8s_vs_3_novalocal_Containerlist.add("VKh6jGHNGYNQYX0zvjuJlCoN3Bo");
		k8s_vs_3_novalocal_Containerlist.add("hec8tzXx4RuZtBEsw8QYE-5NjHk");
		k8s_vs_3_novalocal_Containerlist.add("Q-NcFqVUxATUkWmsveCvfKaeYmQ");
		k8s_vs_3_novalocal_Containerlist.add("f7hRvBiR8y92KKU7qKkrU7fgupk");
		k8s_vs_3_novalocal_Containerlist.add("AKSOnsz8ZwBxVfZBAQZrVZOXGMU");
		k8s_vs_3_novalocal_Containerlist.add("fUnzjvWoVnrwxMhVrhDog_n1ifk");
		k8s_vs_3_novalocal_Containerlist.add("2P_tVjHNDCc9QSgpsMtLt2k1MOc");
		k8s_vs_3_novalocal_Containerlist.add("MwcmkFpGcmfDs4uM6nbZ3P2i8r4");
				
		//Data processing
		while (true) {
			try {
						
				// check for infrastructure and application changes
				InfrastructureData instanaInfrastructureData = new InfrastructureData();
				instanaInfrastructureData.checkForInfrastructureChanges();		
				ApplicationData instanaApplicationData = new ApplicationData();
				instanaApplicationData.checkForApplicationChanges();	
				
				logger.info("start processing metrics");
				// pull metrics from Instana and store into InfluxDB
				Metrics instanaMetrics = new Metrics();
				instanaMetrics.getCPU_Used_Metrics(hostlist);
				instanaMetrics.getMemory_Used_Metrics(hostlist);
				instanaMetrics.getLoad_Metrics(hostlist);
				instanaMetrics.getContainer_CPU_Metrics("R6x_nuhhLZq1wrr9X7aVH1N0nCs",k8s_vs_3_novalocal_Containerlist);
				instanaMetrics.getContainer_Memory_Metrics("R6x_nuhhLZq1wrr9X7aVH1N0nCs",k8s_vs_3_novalocal_Containerlist);
				instanaMetrics.getContainer_IO_Metrics("R6x_nuhhLZq1wrr9X7aVH1N0nCs",k8s_vs_3_novalocal_Containerlist);
				instanaMetrics.getContainer_CPU_Metrics("N0n3QwIlX358zNC_Xp8QKW0NSnY",k8s_vs_2_novalocal_Containerlist);
				instanaMetrics.getContainer_Memory_Metrics("N0n3QwIlX358zNC_Xp8QKW0NSnY",k8s_vs_2_novalocal_Containerlist);
				instanaMetrics.getContainer_IO_Metrics("N0n3QwIlX358zNC_Xp8QKW0NSnY",k8s_vs_2_novalocal_Containerlist);
				instanaMetrics.getService_AvgLatency(servicelist);
				instanaMetrics.getService_Calls(servicelist);
				instanaMetrics.getService_error_rate(servicelist);
				instanaMetrics.getService_Instances(servicelist);
				instanaMetrics.getEventsInformation(instanaMetrics.getEvents());

			} catch (Exception exc) {

			}
		}
	}
	
	/**
	 * Set timestamp for REST calls
	 * @param currentTime
	 */
	public static void setTimestampForDataProcessing(String currentTime) {
		InfrastructureData.currentTime=currentTime;
		ApplicationData.currentTime=currentTime;
		Metrics.currentTime=currentTime;
	}	
}
