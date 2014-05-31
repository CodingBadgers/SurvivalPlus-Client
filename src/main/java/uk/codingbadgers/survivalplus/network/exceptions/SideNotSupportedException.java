package uk.codingbadgers.survivalplus.network.exceptions;

import cpw.mods.fml.relauncher.Side;
import uk.codingbadgers.survivalplus.network.Packet;

public class SideNotSupportedException extends NetworkException {
    public SideNotSupportedException(Side side, Packet packet) {
        super("Side " + side.name() + " is not supported by packet " + packet.getClass().getSimpleName());
    }
}
