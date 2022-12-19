package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var remindersListDb: RemindersDatabase
    private lateinit var remindersDAO: RemindersDao
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @Before
    fun setupDatabase() {
        remindersListDb = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        remindersDAO = remindersListDb.reminderDao()
        remindersLocalRepository =
            RemindersLocalRepository(
                remindersDAO
            )
    }

    @After
    fun closeDatabase() {
        remindersListDb.close()
    }

    @Test
    fun getReminder_saveReminder_shouldReturnReminder() = runBlockingTest {
        //Given
        val reminderDTO = ReminderDTO(
            "title",
            "description",
            "location",
            15.00,
            25.00
        )

        //When
        remindersLocalRepository.saveReminder(reminderDTO)

        val reminder = remindersLocalRepository.getReminder(reminderDTO.id)
        reminder as Success

        //Then
        assertThat(reminder.data.title, `is`(reminderDTO.title))
        assertThat(reminder.data.description, `is`(reminderDTO.description))
        assertThat(reminder.data.location, `is`(reminderDTO.location))
        assertThat(reminder.data.latitude, `is`(reminderDTO.latitude))
        assertThat(reminder.data.longitude, `is`(reminderDTO.longitude))
    }

    @Test
    fun getReminders_saveTwoReminders_shouldReturnCorrectSize() = runBlockingTest {
        val reminder1 = ReminderDTO(
            "title",
            "description",
            "location",
            15.00,
            25.00
        )
        val reminder2 = ReminderDTO(
            "title 2",
            "description 2",
            "location 2",
            5.00,
            10.00
        )

        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)

        val remindersList = remindersLocalRepository.getReminders()
        remindersList as Success

        assertThat(remindersList.data.size, `is`(2))
        assertThat(remindersList.data.contains(reminder1), `is`(true))
        assertThat(remindersList.data.contains(reminder2), `is`(true))
    }

    @Test
    fun getReminders_saveThenDelete_shouldBeEmpty() = runBlockingTest {
        val reminderDTO = ReminderDTO(
            "title",
            "description",
            "location",
            15.00,
            25.00
        )

        remindersLocalRepository.saveReminder(reminderDTO)

        remindersLocalRepository.deleteAllReminders()
        val remindersList = remindersLocalRepository.getReminders()
        remindersList as Success

        assertThat(remindersList.data, `is`(emptyList()))
    }

    @Test
    fun getReminder_withNoReminders_shouldReturnError() = runBlockingTest {
        val reminder = remindersLocalRepository.getReminder("random id") as Error
        val message = reminder.message
        assertThat(message, `is`(notNullValue()))
        assertThat(message, `is`("Reminder not found!"))
    }
}