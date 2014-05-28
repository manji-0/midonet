/*
 * Copyright (c) 2012 Midokura SARL, All Rights Reserved.
 */
package org.midonet.odp.flows;

import java.util.List;
import java.nio.ByteBuffer;

import org.midonet.netlink.NetlinkMessage;
import org.midonet.netlink.NetlinkMessage.AttrKey;
import org.midonet.odp.OpenVSwitch;

public class FlowActionSample implements FlowAction {

    private static final AttrKey<Integer> PROBABILITY =
        AttrKey.attr(OpenVSwitch.FlowAction.SampleAttr.Probability);

    private static final AttrKey<List<FlowAction>> ACTIONS =
        AttrKey.attrNested(OpenVSwitch.FlowAction.SampleAttr.Actions);

    /**
     * u32 port number.
     */
    private int probability;

    private List<FlowAction> actions;

    // This is used for deserialization purposes only.
    FlowActionSample() { }

    FlowActionSample(int probability, List<FlowAction> actions) {
        this.probability = probability;
        this.actions = actions;
    }

    public int serializeInto(ByteBuffer buffer) {
        int nBytes= 0;
        short probId = (short) OpenVSwitch.FlowAction.SampleAttr.Probability;
        short actionsId = (short) OpenVSwitch.FlowAction.SampleAttr.Actions;
        nBytes += NetlinkMessage.writeIntAttr(buffer, probId, probability);
        nBytes += NetlinkMessage.writeAttrSeq(buffer, actionsId, actions,
                                              FlowAction.translator);
        return nBytes;
    }

    @Override
    public boolean deserialize(NetlinkMessage message) {
        try {
            probability = message.getAttrValueInt(PROBABILITY);
            actions = message.getAttrValue(ACTIONS, FlowAction.Builder);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public short attrId() {
        return FlowActionAttr.SAMPLE.getId();
    }

    public int getProbability() {
        return probability;
    }

    public List<FlowAction> getActions() {
        return actions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowActionSample that = (FlowActionSample) o;

        if (probability != that.probability) return false;
        if (actions != null ? !actions.equals(
            that.actions) : that.actions != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = probability;
        result = 31 * result + (actions != null ? actions.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FlowActionSample{" +
            "probability=" + probability +
            ", actions=" + actions +
            '}';
    }
}
