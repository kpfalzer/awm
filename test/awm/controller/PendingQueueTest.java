package awm.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PendingQueueTest {

    @Test
    void add() {
        final PendingQueue pq = new PendingQueue();
        PendingJob j1 = new PendingJob("u1", "h1", "c1", 1, 1, 10, null);
        PendingJob j2 = new PendingJob("u2", "h2", "c2", 1, 1, 20, null);
        PendingJob j3 = new PendingJob("u3", "h3", "c3", 1, 1, 0, null);
        PendingJob j4 = new PendingJob("u4", "h4", "c4", 1, 1, 1000, null);
        PendingJob j5 = new PendingJob("u5", "h5", "c5", 1, 1, -10, null);
        pq.insert(j1, j2, j3, j4, j5);
        assertEquals(5, pq.size());
        PendingJob j6 = new PendingJob("u6", "h6", "c6", 1, 1, 20, null);
        pq.insert(j6);
        assertEquals(6, pq.size());
        PendingJob j7 = new PendingJob("u7", "h7", "c7", 1, 1, 50, null);
        pq.insert(j7);
        assertEquals(7, pq.size());
        PendingJob j8 = new PendingJob("u8", "h8", "c8", 1, 1, -20, null);
        pq.insert(j8);
        assertEquals(8, pq.size());
    }
}