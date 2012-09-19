/*
 * Copyright 2012 Midokura Pte. Ltd.
 */

package com.midokura.midolman.topology

import java.util.UUID
import com.midokura.packets.MAC


object FlowTagger {

    def invalidateAllDeviceFlowsTag(device: UUID): AnyRef =
        (device)

    def invalidateAllMacFlowsTag(bridgeId: UUID, mac: MAC): AnyRef =
        (bridgeId, mac)

    def invalidateAllMacPortFlows(bridgeId: UUID, mac: MAC,
                                  port: UUID): AnyRef =
        (bridgeId, mac, port)

    def invalidateBroadcastFlows(bridgeId: UUID): AnyRef =
        (bridgeId, "broadcast")
}
