package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    private lateinit var remindersDataSource: FakeDataSource
    private lateinit var remindersViewModel: SaveReminderViewModel

    @get:Rule
    var instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        remindersDataSource = FakeDataSource()
        remindersViewModel = SaveReminderViewModel( ApplicationProvider.getApplicationContext(), remindersDataSource )
    }

    @After
    fun cleanUp() = runBlockingTest {
        stopKoin()
        remindersDataSource.deleteAllReminders()
    }

    @Test
    fun checkLoading_saveReminder_shouldReturnSavedReminder() = runBlockingTest {
        //Given - valid ReminderDataItem
        mainCoroutineRule.pauseDispatcher()
        val reminderDataItem = ReminderDataItem(
            "test title",
            "test description",
            "test location",
            15.00,
            25.00
        )

        // When - save ReminderDataItem
        remindersViewModel.saveReminder(reminderDataItem)

        // Then - showLoading & ReminderDataItem is saved successfully
        assertThat(remindersViewModel.showLoading.value, `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(remindersViewModel.showLoading.value, `is`(false))
        assertThat(remindersViewModel.showToast.value, `is`("Reminder Saved !"))

        val reminder = remindersDataSource.getReminder(reminderDataItem.id)
        reminder as Result.Success

        assertThat(reminder.data.title, `is`(reminderDataItem.title))
        assertThat(reminder.data.description, `is`(reminderDataItem.description))
        assertThat(reminder.data.location, `is`(reminderDataItem.location))
        assertThat(reminder.data.latitude, `is`(reminderDataItem.latitude))
        assertThat(reminder.data.longitude, `is`(reminderDataItem.longitude))
    }

    @Test
    fun validateReminder_emptyTitle_shouldReturnFalse() {
        //Given - invalid ReminderDataItem
        val reminder = ReminderDataItem(
            "",
            "test description",
            "test location",
            15.00,
            25.00
        )

        // When - save & validate ReminderDataItem
        val result = remindersViewModel.validateEnteredData(reminder)

        // Then - ReminderDataItem errors out
        assertThat(result, `is`(false))
        assertThat(remindersViewModel.showSnackBarInt.value, `is`(R.string.err_enter_title))
    }

    @Test
    fun validateReminder_nullTitle_shouldReturnFalse() {
        //Given - invalid ReminderDataItem
        val reminder = ReminderDataItem(
            null,
            "test description",
            "test location",
            15.00,
            25.00
        )

        // When - save & validate ReminderDataItem
        val result = remindersViewModel.validateEnteredData(reminder)

        // Then - ReminderDataItem errors out
        assertThat(result, `is`(false))
        assertThat(remindersViewModel.showSnackBarInt.value, `is`(R.string.err_enter_title))
    }

    @Test
    fun validateReminder_emptyLocation_shouldReturnFalse() {
        //Given - invalid ReminderDataItem
        val reminder = ReminderDataItem(
            "test title",
            "test description",
            "",
            15.00,
            25.00
        )

        // When - save & validate ReminderDataItem
        val result = remindersViewModel.validateEnteredData(reminder)

        // Then - ReminderDataItem errors out
        assertThat(result, `is`(false))
        assertThat(remindersViewModel.showSnackBarInt.value, `is`(R.string.err_select_location))
    }

    @Test
    fun validateReminder_nullLocation_shouldReturnFalse() {
        //Given - invalid ReminderDataItem
        val reminder = ReminderDataItem(
            "test title",
            "test description",
            null,
            15.00,
            25.00
        )

        // When - save & validate ReminderDataItem
        val result = remindersViewModel.validateEnteredData(reminder)

        // Then - ReminderDataItem errors out
        assertThat(result, `is`(false))
        assertThat(remindersViewModel.showSnackBarInt.value, `is`(R.string.err_select_location))
    }

    @Test
    fun validateReminder_validData_shouldReturnTrue() {
        //Given - valid ReminderDataItem
        val reminder = ReminderDataItem(
            "test title",
            "test description",
            "test location",
            15.00,
            25.00
        )

        // When - save & validate ReminderDataItem
        val result = remindersViewModel.validateEnteredData(reminder)

        // Then - ReminderDataItem succeeds
        assertThat(result, `is`(true))
    }

}