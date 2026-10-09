package cachet.plugins.health

import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.metadata.Metadata
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class HealthDataConverterTest {
    private val start = Instant.parse("2026-10-01T22:00:00Z")
    private val end = Instant.parse("2026-10-02T06:00:00Z")
    // The Metadata constructor is internal in the pinned connect-client, so the test mocks it.
    private val metadata = mockk<Metadata>(relaxed = true) {
        every { id } returns "id"
        every { dataOrigin.packageName } returns "com.example"
    }

    @Test
    fun sleepSessionCarriesThePackageNameInSourceId() {
        val record = SleepSessionRecord(
            startTime = start,
            startZoneOffset = null,
            endTime = end,
            endZoneOffset = null,
            metadata = metadata,
        )

        val maps = HealthDataConverter().convertRecord(record, "SLEEP_SESSION")

        assertEquals(1, maps.size)
        assertEquals("com.example", maps.single()["source_id"])
    }

    @Test
    fun sleepStageCarriesThePackageNameInSourceId() {
        val stage = SleepSessionRecord.Stage(start, end, SleepSessionRecord.STAGE_TYPE_LIGHT)

        val maps = HealthDataConverter().convertRecordStage(stage, "SLEEP_LIGHT", metadata)

        assertEquals(1, maps.size)
        assertEquals("com.example", maps.single()["source_id"])
    }
}
