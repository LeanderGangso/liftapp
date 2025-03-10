package com.patrykandpatryk.liftapp.core.mapper

import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.extension.getOrPut
import javax.inject.Inject

class BodyMeasurementEntryToChartEntryMapper @Inject constructor() {

    operator fun invoke(input: List<BodyMeasurementEntry>): List<List<ChartEntry>> =
        input.foldIndexed(ArrayList<ArrayList<ChartEntry>>()) { index, chartEntries, entry ->
            val reversedIndex = input.lastIndex - index

            when (val value = entry.value) {
                is BodyMeasurementValue.DoubleValue -> {
                    chartEntries.getOrPut(0) { ArrayList() }.add(entryOf(reversedIndex, value.left))

                    chartEntries
                        .getOrPut(1) { ArrayList() }
                        .add(entryOf(reversedIndex, value.right))
                }
                is BodyMeasurementValue.SingleValue -> {
                    chartEntries
                        .getOrPut(0) { ArrayList() }
                        .add(entryOf(reversedIndex, value.value))
                }
            }

            chartEntries
        }
}
