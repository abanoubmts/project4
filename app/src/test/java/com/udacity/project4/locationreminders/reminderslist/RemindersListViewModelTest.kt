package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.MainCoroutine
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.runner.RunWith
import org.hamcrest.core.IsNot
import org.junit.*
import org.koin.core.context.stopKoin
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var MainCoroutine = MainCoroutine()


    private lateinit var remindersRepository: FakeDataSource


    private lateinit var viewModel: RemindersListViewModel

    @Before
    fun setupViewModel() {
        remindersRepository = FakeDataSource()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
    }

    @After
    fun tearDown() {
        stopKoin()
    }



    @Test
    fun loadReminders_showLoading() {
        MainCoroutine.pauseDispatcher()

        viewModel.loadReminders()

        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()

        MainCoroutine.resumeDispatcher()

        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()

    }

    // This method is deprecated in favor of runTest that
    // used to execute in the presence of calls to delay without causing your test to take extra time.
    /*
   In the below test case, I have inserted a reminder location
   object properties . So this validate function should return
   true.
   To check that, used Junit assert that  function
   to check if a specific value match to an expected one*/
    @Test
    fun loadReminders_remainderListNotEmpty() = MainCoroutine.runBlockingTest  {
        val reminder = ReminderDTO("My application ", "project 4 data ", "cairo", 15.454202, 17.599545)

        remindersRepository.saveReminder(reminder)
        viewModel.loadReminders()

        assertThat(viewModel.remindersList.getOrAwaitValue()).isNotEmpty()
    }


    // TEST DURING load reminders and control threadings by coroutines and its dispatcher
    @Test
    fun loadReminders_updateSnackBarValue() {
        MainCoroutine.pauseDispatcher()

        remindersRepository.error_flag = true

        viewModel.loadReminders()

        MainCoroutine.resumeDispatcher()

        assertThat(viewModel.showSnackBar.getOrAwaitValue()).isEqualTo("Error getting reminders")
    }
}