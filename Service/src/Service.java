import java.util.ArrayList;

public class Service {

	public static void main(String[] args) throws Exception {

		String currentTime = String.valueOf(System.currentTimeMillis());
		setTimestampForDataProcessing(currentTime);
		
		// static values for sock-shop environment
		ArrayList<String> hostlist = new ArrayList<>();
		hostlist.add("R6x_nuhhLZq1wrr9X7aVH1N0nCs");
		hostlist.add("N0n3QwIlX358zNC_Xp8QKW0NSnY");
		ArrayList<String> containerlist2 = new ArrayList<>();
		containerlist2.add("GfVYqknxuiLBWvCgihkltUKoPgU");
		containerlist2.add("rx3xhns6KKaQax1ghrjHje9_j6k");
		containerlist2.add("7uHhWaExHla_-v2wFtzkS5CT-TA");
		ArrayList<String> servicelist = new ArrayList<>();
		servicelist.add("b9xeCfs_8x6YIqZ06Rv3bzxpOHA");
		servicelist.add("sHN0tA3GC3NXC926h3oNBxq8ROA");
		ArrayList<String> containerlist1 = new ArrayList<>();
		containerlist1.add("X3zF6QXImBlWIufTyR9MlKpqXrQ");
		containerlist1.add("nf7fy9kn632CgwBmIQY13IqGSLE");
		containerlist1.add("KXxcRow3mOUOjHbzO1l4IFI3Zq8");
		containerlist1.add("ZKdcVJ6Io9m8bykScofxAJCb9hQ");
		containerlist1.add("Q-NcFqVUxATUkWmsveCvfKaeYmQ");
		containerlist1.add("2P_tVjHNDCc9QSgpsMtLt2k1MOc");
		containerlist1.add("AKSOnsz8ZwBxVfZBAQZrVZOXGMU");
		containerlist1.add("fUnzjvWoVnrwxMhVrhDog_n1ifk");
		
		
		
		
		InfrastructureData instanaInfrastructureData = new InfrastructureData();
		instanaInfrastructureData.checkForInfrastructureChanges();
		ApplicationData instanaApplicationData = new ApplicationData();
		instanaApplicationData.checkForApplicationChanges();
//		while (true) {
//			try {
//				
//				
//				
//				instanaMetrics.getCPU_Used_Metrics(hostlist);
//				instanaMetrics.getMemory_Used_Metrics(hostlist);
//				getLoad_Metrics(hostlist);
//				getContainer_CPU_Metrics(containerlist1);
//				getContainer_Memory_Metrics(containerlist1);
//				getContainer_IO_Metrics(containerlist1);
//				getContainer_CPU_Metrics(containerlist2);
//				getContainer_Memory_Metrics(containerlist2);
//				getContainer_IO_Metrics(containerlist2);
//				getService_AvgLatency(servicelist);
//				getService_Calls(servicelist);
//				getService_error_rate(servicelist);
//				getService_Instances(servicelist);
//				getComponentInformantion("ujAP46NqxH64mzMVmlVkB0gHNd0");
//			} catch (Exception exc) {
//
//			}
//
//		}
//		
	}
	public static void setTimestampForDataProcessing(String currentTime) {
		InfrastructureData.currentTime=currentTime;
		ApplicationData.currentTime=currentTime;
	}
}
