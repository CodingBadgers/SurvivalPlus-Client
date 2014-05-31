package uk.codingbadgers.survivalplus.network;

public interface NetworkHandler {

    public void registerPacket(Class<? extends Packet> packet);

    public void sendPacket(Packet packet);

    public void init();

    public void lock();

}
