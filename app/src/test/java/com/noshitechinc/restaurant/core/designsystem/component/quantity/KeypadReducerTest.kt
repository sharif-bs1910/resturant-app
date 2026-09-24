package com.noshitechinc.restaurant.core.designsystem.component.quantity

import kotlin.test.Test
import kotlin.test.assertEquals

class KeypadReducerTest {
    private fun type(mode: KeypadMode, vararg keys: KeypadKey, start: String = ""): String =
        keys.fold(start) { acc, key -> KeypadReducer.reduce(acc, key, mode) }

    private fun d(n: Int) = KeypadKey.Digit(n)

    @Test
    fun `integer mode drops leading zero and respects max length`() {
        assertEquals("5", type(KeypadMode.Integer, d(0), d(5)))
        assertEquals("123456789", type(KeypadMode.Integer, *Array(12) { d(it % 9 + 1) }))
    }

    @Test
    fun `pin mode keeps leading zeros`() {
        assertEquals("0012", type(KeypadMode.Pin, d(0), d(0), d(1), d(2)))
    }

    @Test
    fun `decimal mode allows one separator and two fraction digits`() {
        assertEquals("0.", type(KeypadMode.Decimal, KeypadKey.Decimal))
        assertEquals("12.34", type(KeypadMode.Decimal, d(1), d(2), KeypadKey.Decimal, KeypadKey.Decimal, d(3), d(4), d(5)))
    }

    @Test
    fun `currency mode appends double zero and trims leading zeros`() {
        assertEquals("500", type(KeypadMode.Currency, d(0), d(5), KeypadKey.DoubleZero))
        assertEquals("", type(KeypadMode.Currency, KeypadKey.DoubleZero))
    }

    @Test
    fun `backspace and clear`() {
        assertEquals("12", type(KeypadMode.Integer, d(1), d(2), d(3), KeypadKey.Backspace))
        assertEquals("", type(KeypadMode.Integer, d(1), d(2), KeypadKey.Clear))
        assertEquals("", type(KeypadMode.Integer, KeypadKey.Backspace))
    }

    @Test
    fun `keys not valid for a mode are ignored`() {
        assertEquals("1", type(KeypadMode.Integer, d(1), KeypadKey.Decimal))
        assertEquals("1", type(KeypadMode.Pin, d(1), KeypadKey.DoubleZero))
    }
}
