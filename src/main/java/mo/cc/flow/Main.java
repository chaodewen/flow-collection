package mo.cc.flow;

import java.util.HashSet;
import java.util.Set;

import mo.cc.Utils;
import mo.cc.flow.resource.FlowResource;

public class Main {
	public static void main(String[] args) {
		Set<Class<?>> clazzs = new HashSet<Class<?>>();
		clazzs.add(FlowResource.class);
		Utils.runService(clazzs, "8080");
	}
}