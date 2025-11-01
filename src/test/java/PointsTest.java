import com.scoreboard.service.scorecalculation.Points;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointsTest {
    @Test
    void getValue_shouldReturnCorrectStringValues() {
        assertEquals("0", Points.ZERO.getValue());
        assertEquals("15", Points.FIFTEEN.getValue());
        assertEquals("30", Points.THIRTY.getValue());
        assertEquals("40", Points.FORTY.getValue());
        assertEquals("AD", Points.ADVANTAGE.getValue());
    }

    @Test
    void next_fromZero_shouldReturnFifteen() {
        assertEquals(Points.FIFTEEN, Points.ZERO.next());
    }

    @Test
    void next_fromFifteen_shouldReturnThirty() {
        assertEquals(Points.THIRTY, Points.FIFTEEN.next());
    }

    @Test
    void next_fromThirty_shouldReturnForty() {
        assertEquals(Points.FORTY, Points.THIRTY.next());
    }

    @Test
    void next_fromForty_shouldReturnAdvantage() {
        assertEquals(Points.ADVANTAGE, Points.FORTY.next());
    }

    @Test
    void next_fromAdvantage_shouldThrowIllegalStateException() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                Points.ADVANTAGE::next
        );

        assertEquals("Cannot advance beyond ADVANTAGE. Game should be finished.",
                exception.getMessage());
    }

    @Test
    void next_multipleProgression_shouldAdvanceCorrectly() {
        Points current = Points.ZERO;
        current = current.next();
        assertEquals(Points.FIFTEEN, current);

        current = current.next();
        assertEquals(Points.THIRTY, current);

        current = current.next();
        assertEquals(Points.FORTY, current);

        current = current.next();
        assertEquals(Points.ADVANTAGE, current);
    }

    @Test
    void canAdvance_zeroToForty_shouldReturnTrue() {
        assertTrue(Points.ZERO.canAdvance());
        assertTrue(Points.FIFTEEN.canAdvance());
        assertTrue(Points.THIRTY.canAdvance());
        assertTrue(Points.FORTY.canAdvance());
    }

    @Test
    void canAdvance_advantage_shouldReturnFalse() {
        assertFalse(Points.ADVANTAGE.canAdvance());
    }
    

    @Test
    void enumValues_shouldContainExactlyFiveElements() {
        Points[] values = Points.values();
        assertEquals(5, values.length);
    }

    @Test
    void enumValues_shouldBeInCorrectOrder() {
        Points[] values = Points.values();
        assertEquals(Points.ZERO, values[0]);
        assertEquals(Points.FIFTEEN, values[1]);
        assertEquals(Points.THIRTY, values[2]);
        assertEquals(Points.FORTY, values[3]);
        assertEquals(Points.ADVANTAGE, values[4]);
    }

    @Test
    void valueOf_withValidName_shouldReturnCorrectEnum() {
        assertEquals(Points.ZERO, Points.valueOf("ZERO"));
        assertEquals(Points.FIFTEEN, Points.valueOf("FIFTEEN"));
        assertEquals(Points.THIRTY, Points.valueOf("THIRTY"));
        assertEquals(Points.FORTY, Points.valueOf("FORTY"));
        assertEquals(Points.ADVANTAGE, Points.valueOf("ADVANTAGE"));
    }

    @Test
    void valueOf_withInvalidName_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Points.valueOf("INVALID"));
    }

    @Test
    void valueOf_withNullName_shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> Points.valueOf(null));
    }

    @Test
    void points_shouldBeComparable() {
        Points point1 = Points.ZERO;
        Points point2 = Points.ZERO;
        assertSame(point1, point2);
    }

    @Test
    void toString_shouldReturnEnumName() {
        assertEquals("ZERO", Points.ZERO.toString());
        assertEquals("FIFTEEN", Points.FIFTEEN.toString());
        assertEquals("THIRTY", Points.THIRTY.toString());
        assertEquals("FORTY", Points.FORTY.toString());
        assertEquals("ADVANTAGE", Points.ADVANTAGE.toString());
    }
}
