package edu.utd.safeguard.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/** Domain prototype for FR-09, FR-10, FR-11 and FR-17. */
public final class SafeWalkSession {

    public enum State { ACTIVE, ARRIVED, EXPIRED, CANCELLED }

    private final String studentId;
    private final String destination;
    private Instant expiresAt;
    private State state = State.ACTIVE;
    private boolean extensionUsed = false;

    public SafeWalkSession(String studentId, String destination,
                           Instant startedAt, Duration duration) {
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("Student ID is required");
        }
        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Destination is required");
        }
        Objects.requireNonNull(startedAt, "Start time is required");
        requirePositive(duration);
        this.studentId = studentId;
        this.destination = destination;
        this.expiresAt = startedAt.plus(duration);
    }

    /** FR-10: one extension only, while the session is active. */
    public void extend(Duration additionalTime) {
        requireActive();
        if (extensionUsed) {
            throw new IllegalStateException("Only one extension allowed");
        }
        requirePositive(additionalTime);
        expiresAt = expiresAt.plus(additionalTime);
        extensionUsed = true;
    }

    /** FR-11: student confirms arrival. */
    public void markArrived() {
        requireActive();
        state = State.ARRIVED;
    }

    /** FR-11: student cancels the walk. */
    public void cancel() {
        requireActive();
        state = State.CANCELLED;
    }

    /** FR-12: called by the timer/controller; returns true only on a new expiry. */
    public boolean expireIfDue(Instant now) {
        Objects.requireNonNull(now, "Current time is required");
        if (state == State.ACTIVE && !now.isBefore(expiresAt)) {
            state = State.EXPIRED;
            return true;
        }
        return false;
    }

    private void requireActive() {
        if (state != State.ACTIVE) {
            throw new IllegalStateException("Session is not active");
        }
    }

    private static void requirePositive(Duration duration) {
        if (duration == null || duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
    }

    public State getState() { return state; }
    public String getStudentId() { return studentId; }
    public String getDestination() { return destination; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isExtensionUsed() { return extensionUsed; }
}
