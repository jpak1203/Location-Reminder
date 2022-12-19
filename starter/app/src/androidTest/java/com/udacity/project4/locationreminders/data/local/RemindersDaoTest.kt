package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var remindersListDb: RemindersDatabase

    @Before
    fun initDatabase() {
        remindersListDb = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDatabase() = remindersListDb.close()

    @Test
    fun saveReminder_getReminderDetail_shouldExistInDb() = runBlockingTest {
        // Given
        val reminderDTO = ReminderDTO(
            "title",
            "description",
            "location",
            15.00,
            25.00
        )

        // When
        remindersListDb.reminderDao().saveReminder(reminderDTO)

        val reminderDBItem = remindersListDb.reminderDao().getReminderById(reminderDTO.id)
        reminderDBItem as ReminderDTO

        // Then
        assertThat(reminderDBItem.id, `is`(reminderDTO.id))
        assertThat(reminderDBItem.title, `is`(reminderDTO.title))
        assertThat(reminderDBItem.description, `is`(reminderDTO.description))
        assertThat(reminderDBItem.latitude, `is`(reminderDTO.latitude))
        assertThat(reminderDBItem.longitude, `is`(reminderDTO.longitude))
    }

    @Test
    fun deleteAllReminders_withSavedReminder_shouldBeEmpty() = runBlockingTest {
        // Given
        val reminderDTO = ReminderDTO(
            "title",
            "description",
            "location",
            15.00,
            25.00
        )
        // When
        remindersListDb.reminderDao().saveReminder(reminderDTO)

        val reminderDBItem = remindersListDb.reminderDao().getReminderById(reminderDTO.id)
        reminderDBItem as ReminderDTO

        // Then
        assertThat(reminderDBItem.id, `is`(reminderDTO.id))
        assertThat(reminderDBItem.title, `is`(reminderDTO.title))
        assertThat(reminderDBItem.description, `is`(reminderDTO.description))
        assertThat(reminderDBItem.latitude, `is`(reminderDTO.latitude))
        assertThat(reminderDBItem.longitude, `is`(reminderDTO.longitude))

        // When
        remindersListDb.reminderDao().deleteAllReminders()
        val reminderDbList = remindersListDb.reminderDao().getReminders()

        // Then
        assertThat(reminderDbList.isEmpty(), `is`(true))
    }

    @Test
    fun getReminders_withTwoReminders_shouldReturnCorrectSize() = runBlockingTest {
        //Given there are two reminders
        val testFirstReminder = ReminderDTO(
            "title",
            "description",
            "location",
            15.00,
            25.00
        )
        val testSecondReminder = ReminderDTO(
            "title 2",
            "description 2",
            "location 2",
            5.00,
            10.00
        )
        //When save both reminders one after another
        remindersListDb.reminderDao().saveReminder(testFirstReminder)
        remindersListDb.reminderDao().saveReminder(testSecondReminder)

        val reminderDBList = remindersListDb.reminderDao().getReminders()

        //Then check the size is two
        assertThat(reminderDBList.size, `is`(2))
    }
}