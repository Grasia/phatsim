package phat;

import phat.config.AgentConfigurator;
import phat.config.BodyConfigurator;
import phat.config.DeviceConfigurator;
import phat.config.HouseConfigurator;
import phat.config.WorldConfigurator;

public interface PHATInitializer {
	public void initWorld(WorldConfigurator worldConfig);
	public void initHouse(HouseConfigurator houseConfig);
	public void initBodies(BodyConfigurator bodyConfig);
        public void initDevices(DeviceConfigurator deviceConfig);
	public void initAgents(AgentConfigurator agentsConfig);
        public String getTittle();
}
