package com.p3achb0t._runestar_interfaces

interface Actor : Entity {
    fun getDefaultHeight(): Int
    fun getFalse0(): Boolean
    fun getHeadbars(): IterableNodeDeque
    fun getHeightOffset(): Int
    fun getHitmarkCount(): Byte
    fun getHitmarkCycles(): IntArray
    fun getHitmarkTypes(): IntArray
    fun getHitmarkTypes2(): IntArray
    fun getHitmarkValues(): IntArray
    fun getHitmarkValues2(): IntArray
    fun getIsAutoChatting(): Boolean
    fun getMovementFrame(): Int
    fun getMovementFrameCycle(): Int
    fun getMovementSequence(): Int
    fun getNpcCycle(): Int
    fun getOrientation(): Int
    fun getOverheadText(): String
    fun getOverheadTextColor(): Int
    fun getOverheadTextCyclesRemaining(): Int
    fun getOverheadTextEffect(): Int
    fun getPathLength(): Int
    fun getPathTraversed(): ByteArray
    fun getPathX(): IntArray
    fun getPathY(): IntArray
    fun getPlayerCycle(): Int
    fun getReadySequence(): Int
    fun getRunSequence(): Int
    fun getSequence(): Int
    fun getSequenceDelay(): Int
    fun getSequenceFrame(): Int
    fun getSequenceFrameCycle(): Int
    fun getSize(): Int
    fun getSpotAnimation(): Int
    fun getSpotAnimationFrame(): Int
    fun getSpotAnimationFrameCycle(): Int
    fun getTargetIndex(): Int
    fun getTurnLeftSequence(): Int
    fun getTurnRightSequence(): Int
    fun getWalkBackSequence(): Int
    fun getWalkLeftSequence(): Int
    fun getWalkRightSequence(): Int
    fun getWalkSequence(): Int
    fun getX(): Int
    fun getY(): Int
    fun get__ah(): Boolean
    fun get__ax(): Boolean
    fun get__au(): Int
    fun get__bb(): Int
    fun get__bc(): Int
    fun get__be(): Int
    fun get__bf(): Int
    fun get__bi(): Int
    fun get__bp(): Int
    fun get__br(): Int
    fun get__bt(): Int
    fun get__cb(): Int
    fun get__cf(): Int
    fun get__ck(): Int
    fun get__co(): Int
    fun get__cp(): Int
    fun get__cx(): Int
}
