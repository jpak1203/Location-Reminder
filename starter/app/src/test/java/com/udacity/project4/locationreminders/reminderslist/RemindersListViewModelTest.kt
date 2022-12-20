package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result.*
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

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var remindersDataSource: FakeDataSource
    private lateinit var remindersViewModel: RemindersListViewModel

    @get:Rule
    var instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        remindersDataSource = FakeDataSource()
        remindersViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersDataSource)
    }

    @After
    fun cleanUp() = runBlockingTest {
        stopKoin()
        remindersDataSource.deleteAllReminders()
    }

    @Test
    fun getRemindersViewModel_showLoading_shouldReturnTrueThenFalse() {
        mainCoroutineRule.pauseDispatcher()
        remindersViewModel.loadReminders()

        // Then
        assertThat(remindersViewModel.showLoading.value, `is`(true))

        // When
        mainCoroutineRule.resumeDispatcher()

        // Then
        assertThat(remindersViewModel.showLoading.value, `is`(false))
    }

    @Test
    fun getRemindersViewModel_withNoData_shouldReturnTrue() {
        remindersViewModel.loadReminders()
        assertThat(remindersViewModel.showNoData.value, `is`(true))
    }

    @Test
    fun getRemindersViewModel_withData_shouldReturnError() = runBlockingTest {
        remindersDataSource.setReturnError(true)
        // Given
        remindersDataSource.saveReminder(
            ReminderDTO(
                "title",
                "description",
                "location",
                15.00,
                25.00
            )
        )

        remindersViewModel.loadReminders()

        assertThat(remindersViewModel.showNoData.value, `is`(true))
    }

    @Test
    fun getReminders_withData_shouldReturnCorrectSize() = runBlockingTest {
        // Given
        remindersDataSource.saveReminder(
            ReminderDTO(
                "title",
                "description",
                "location",
                15.00,
                25.00
            )
        )

        remindersDataSource.saveReminder(
            ReminderDTO(
                "title 2",
                "description 2",
                "location 2",
                5.00,
                10.00
            )
        )
        // When
        val reminderList = remindersDataSource.getReminders()
        reminderList as Success

        // Then
        assertThat(reminderList.data.size, `is`(2))
    }
}