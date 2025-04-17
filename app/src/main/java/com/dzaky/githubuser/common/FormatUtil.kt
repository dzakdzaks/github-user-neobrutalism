package com.dzaky.githubuser.common

import java.util.Locale

/**
 * Formats a number with K for thousands and M for millions.
 * Uses US locale for consistent number formatting.
 *
 * @param number The number to format
 * @return Formatted string (e.g. "1.5K" for 1500)
 */
fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format(Locale.US, "%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format(Locale.US, "%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}