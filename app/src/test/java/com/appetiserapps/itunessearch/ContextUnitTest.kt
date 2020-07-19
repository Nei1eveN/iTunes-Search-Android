package com.appetiserapps.itunessearch

import android.content.Context
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

private const val FAKE_STRING = "Artist"

/*@RunWith(MockitoJUnitRunner::class)*/
class ContextUnitTest {

    @Mock
    private val context: Context? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        /*whenever(context?.getString(R.string.album)).thenReturn("")*/
    }

    @Test
    fun `string not null`() {
        whenever(context?.getString(R.string.album)).thenReturn("Album")
        val stringValue = context?.getString(R.string.album)
        assertNotNull(stringValue)
    }

    @Test
    fun `string empty`() {
        whenever(context?.getString(R.string.album)).thenReturn("")
        val stringValue = context?.getString(R.string.album)
        assertTrue(stringValue?.isEmpty() == true)
    }

    @Test
    fun stringMustBeEqual() {
        whenever(context?.getString(R.string.album)).thenReturn(FAKE_STRING)
        val stringValue = context?.getString(R.string.album)
        assertTrue(stringValue == FAKE_STRING)
    }

    @Test
    fun stringMustNotBeEqual() {
        whenever(context?.getString(R.string.album)).thenReturn("Album")
        val stringValue = context?.getString(R.string.album)
        assertFalse(stringValue == FAKE_STRING)
    }

    @Test
    fun `string must be null`() {
        whenever(context?.getString(R.string.album)).thenReturn(null)
        val stringValue = context?.getString(R.string.album)
        assertEquals(null, stringValue)
    }

}