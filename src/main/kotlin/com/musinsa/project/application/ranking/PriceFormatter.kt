package com.musinsa.project.application.ranking

import java.math.BigDecimal
import java.text.DecimalFormat

object PriceFormatter {
    private val decimalFormat: DecimalFormat = DecimalFormat("#,###")

    fun formatted(value: BigDecimal) = decimalFormat.format(value)
}
