package com.trananticheat.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class PlayerData {

    private final UUID uuid;
    private Player player;

    private Location lastSample;
    private long sampleTime;

    private int airTicks;
    private int verticalAscendTicks;
    private double fallStartY;

    private int hoverTicks;
    private long hoverSince;
    private double hoverHorizontal;

    private double violations;
    private long lastAlert;

    private boolean joinScanned;

    private float lastYaw = Float.NaN;
    private int yawExcessTicks;

    private long lastAttackTime;
    private UUID lastAttackTarget;
    private int fastAttackCount;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID uuid() {
        return uuid;
    }

    public Player player() {
        return player;
    }

    public void player(Player p) {
        this.player = p;
    }

    public Location lastSample() {
        return lastSample;
    }

    public void lastSample(Location l) {
        this.lastSample = l;
    }

    public long sampleTime() {
        return sampleTime;
    }

    public void sampleTime(long t) {
        this.sampleTime = t;
    }

    public int airTicks() {
        return airTicks;
    }

    public void airTicks(int v) {
        this.airTicks = v;
    }

    public int verticalAscendTicks() {
        return verticalAscendTicks;
    }

    public void verticalAscendTicks(int v) {
        this.verticalAscendTicks = v;
    }

    public double fallStartY() {
        return fallStartY;
    }

    public void fallStartY(double v) {
        this.fallStartY = v;
    }

    public int hoverTicks() {
        return hoverTicks;
    }

    public void hoverTicks(int v) {
        this.hoverTicks = v;
    }

    public long hoverSince() {
        return hoverSince;
    }

    public void hoverSince(long t) {
        this.hoverSince = t;
    }

    public double hoverHorizontal() {
        return hoverHorizontal;
    }

    public void hoverHorizontal(double v) {
        this.hoverHorizontal = v;
    }

    public double violations() {
        return violations;
    }

    public void violations(double v) {
        this.violations = v;
    }

    public long lastAlert() {
        return lastAlert;
    }

    public void lastAlert(long t) {
        this.lastAlert = t;
    }

    public boolean joinScanned() {
        return joinScanned;
    }

    public void joinScanned(boolean b) {
        this.joinScanned = b;
    }

    public float lastYaw() {
        return lastYaw;
    }

    public void lastYaw(float v) {
        this.lastYaw = v;
    }

    public int yawExcessTicks() {
        return yawExcessTicks;
    }

    public void yawExcessTicks(int v) {
        this.yawExcessTicks = v;
    }

    public long lastAttackTime() {
        return lastAttackTime;
    }

    public void lastAttackTime(long v) {
        this.lastAttackTime = v;
    }

    public UUID lastAttackTarget() {
        return lastAttackTarget;
    }

    public void lastAttackTarget(UUID v) {
        this.lastAttackTarget = v;
    }

    public int fastAttackCount() {
        return fastAttackCount;
    }

    public void fastAttackCount(int v) {
        this.fastAttackCount = v;
    }
}