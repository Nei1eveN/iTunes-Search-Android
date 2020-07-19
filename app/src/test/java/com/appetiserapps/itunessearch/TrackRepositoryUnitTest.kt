package com.appetiserapps.itunessearch

import com.appetiserapps.itunessearch.data.model.SearchResult
import com.appetiserapps.itunessearch.data.model.Track
import com.appetiserapps.itunessearch.data.repository.TrackRepository
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

private const val FAKE_STRING = "Artist"

@RunWith(MockitoJUnitRunner::class)
class TrackRepositoryUnitTest {

    @Mock
    lateinit var repositoryMock: TrackRepository

    lateinit var trackFromDao: Track

    lateinit var searchFromApi: SearchResult

    @Before
    fun setup() {
        /*MockitoAnnotations.initMocks(this)*/

        trackFromDao =
            Track(trackId = 99, collectionId = 100, artistId = 101, wrapperType = FAKE_STRING)
        repositoryMock.addItem(trackFromDao)
        verify(repositoryMock, times(1)).addItem(trackFromDao)
        whenever(repositoryMock.findByCollectionId(100)).thenReturn(trackFromDao)
        whenever(repositoryMock.findById(99)).thenReturn(trackFromDao)
        /*repositoryMock = TrackRepository(
            realm = mockRealm,
            trackDao = trackDao,
            searchApiClient = mockSearchApiClient
        )*/

    }

    @Test
    fun `trackId null`() {
        val nullId = repositoryMock.findById(98)
        assertEquals(null, nullId)
    }

    @Test
    fun `trackId not null`() {
        val existingItem = repositoryMock.findById(99)
        assertEquals(existingItem, trackFromDao)
    }

    @Test
    fun `expected collection id null`() {
        val nullItem = repositoryMock.findByCollectionId(101)
        verify(repositoryMock, times(1)).findByCollectionId(101)
        assertEquals(nullItem, null)
    }

    @Test
    fun `expected collection id not null`() {
        repositoryMock.findByCollectionId(100)
        verify(repositoryMock, times(1)).findByCollectionId(100)
        assertEquals(repositoryMock.findByCollectionId(100), trackFromDao)
    }

    @Test
    fun `add new item`() {
        val track1 = Track(trackId = 100, collectionId = 101, artistName = FAKE_STRING)
        repositoryMock.addItem(track1)
        verify(repositoryMock, times(1)).addItem(track1)

        repositoryMock.findById(100)
        verify(repositoryMock, times(1)).findById(100)
        whenever(repositoryMock.findById(100)).thenReturn(track1)

        assertEquals(repositoryMock.findById(100), track1)
    }

    @Test
    fun `add new item with negative track number`() {
        val track1 = Track(trackId = -100, collectionId = 101, artistName = FAKE_STRING)
        repositoryMock.addItem(track1)
        verify(repositoryMock, times(1)).addItem(track1)

        repositoryMock.findById(-100)
        verify(repositoryMock, times(1)).findById(-100)
        whenever(repositoryMock.findById(-100)).thenReturn(track1)

        assertNotNull(repositoryMock.findById(-100))
    }

    /*@Test
    fun `test search api call`() {

    }*/

}