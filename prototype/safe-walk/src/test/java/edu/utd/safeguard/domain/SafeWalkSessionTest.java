package edu.utd.safeguard.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SafeWalkSessionTest {

    private static final Instant START = Instant.parse("2026-11-12T20:00:00Z");
    private static final Duration FIFTEEN_MIN = Duration.ofMinutes(15);

    private SafeWalkSession session() {
        return new SafeWalkSession("student-01", "Library", START, FIFTEEN_MIN);
    }

    @Test @DisplayName("TC-01 valid session starts ACTIVE with correct deadline")
    void tc01_createsActiveSession() {
        SafeWalkSession s = session();
        assertEquals(SafeWalkSession.State.ACTIVE, s.getState());
        assertEquals("Library", s.getDestination());
        assertEquals(START.plus(FIFTEEN_MIN), s.getExpiresAt());
        assertFalse(s.isExtensionUsed());
    }

    @Test @DisplayName("TC-02 blank destination is rejected")
    void tc02_rejectsBlankDestination() {
        assertThrows(IllegalArgumentException.class,
            () -> new SafeWalkSession("student-01", " ", START, FIFTEEN_MIN));
    }

    @Test @DisplayName("TC-03 zero duration is rejected")
    void tc03_rejectsZeroDuration() {
        assertThrows(IllegalArgumentException.class,
            () -> new SafeWalkSession("student-01", "Library", START, Duration.ZERO));
    }

    @Test @DisplayName("TC-04 first extension moves the deadline")
    void tc04_firstExtensionMovesDeadline() {
        SafeWalkSession s = session();
        s.extend(Duration.ofMinutes(5));
        assertEquals(START.plus(Duration.ofMinutes(20)), s.getExpiresAt());
        assertTrue(s.isExtensionUsed());
    }

    @Test @DisplayName("TC-05 second extension is rejected")
    void tc05_rejectsSecondExtension() {
        SafeWalkSession s = session();
        s.extend(Duration.ofMinutes(5));
        assertThrows(IllegalStateException.class, () -> s.extend(Duration.ofMinutes(5)));
    }

    @Test @DisplayName("TC-06 student can mark arrived")
    void tc06_markArrived() {
        SafeWalkSession s = session();
        s.markArrived();
        assertEquals(SafeWalkSession.State.ARRIVED, s.getState());
    }

    @Test @DisplayName("TC-07 student can cancel")
    void tc07_cancel() {
        SafeWalkSession s = session();
        s.cancel();
        assertEquals(SafeWalkSession.State.CANCELLED, s.getState());
    }

    @Test @DisplayName("TC-08 session expires exactly at the deadline")
    void tc08_expiresAtDeadline() {
        SafeWalkSession s = session();
        assertTrue(s.expireIfDue(START.plusSeconds(900)));
        assertEquals(SafeWalkSession.State.EXPIRED, s.getState());
    }

    @Test @DisplayName("TC-09 session does not expire before the deadline")
    void tc09_notExpiredBeforeDeadline() {
        SafeWalkSession s = session();
        assertFalse(s.expireIfDue(START.plusSeconds(899)));
        assertEquals(SafeWalkSession.State.ACTIVE, s.getState());
    }

    @Test @DisplayName("TC-10 arrived session never expires")
    void tc10_arrivedSessionDoesNotExpire() {
        SafeWalkSession s = session();
        s.markArrived();
        assertFalse(s.expireIfDue(START.plusSeconds(3600)));
        assertEquals(SafeWalkSession.State.ARRIVED, s.getState());
    }

    @Test @DisplayName("TC-11 no extension after the walk has ended")
    void tc11_rejectsExtensionAfterArrival() {
        SafeWalkSession s = session();
        s.markArrived();
        assertThrows(IllegalStateException.class, () -> s.extend(Duration.ofMinutes(5)));
    }

    @Test @DisplayName("TC-12 expiry is reported only once")
    void tc12_expiryReportedOnlyOnce() {
        SafeWalkSession s = session();
        assertTrue(s.expireIfDue(START.plusSeconds(900)));
        assertFalse(s.expireIfDue(START.plusSeconds(1000)));
    }
}
