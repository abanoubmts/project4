package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutine
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    //TODO: provide testing to the SaveReminderView and its live data objects

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutine = MainCoroutine()



    private lateinit var remindersRepository: FakeDataSource

    //Subject under test
    private lateinit var viewModel: SaveReminderViewModel

    @Before
    fun setupViewModel() {
        remindersRepository = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
    }

    @After
    fun     SetupDown() {
        stopKoin()
    }

    @Test
    fun validateEnteredData_TitleIsEmptyAndUpdateSnackBar() {
        val reminder = ReminderDataItem("", "Description", "My work office ", 10.37625, 6.54343)

        Truth.assertThat(viewModel.validateEnteredData(reminder)).isFalse()
        Truth.assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_enter_title)
    }

    @Test
    fun validateEnteredData_LocationIsEmptyAndUpdateSnackBar() {
        val reminder = ReminderDataItem("Title", "Description", "", 12.32323, 10.54343)

        Truth.assertThat(viewModel.validateEnteredData(reminder)).isFalse()
        Truth.assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_select_location)
    }


    @Test
    fun AddReminder_showLoading(){
        val reminder = ReminderDataItem("Title", "Description", "hospital ", 7.32323, 6.54343)
        mainCoroutine.pauseDispatcher()
        viewModel.saveReminder(reminder)
        Truth.assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()
        mainCoroutine.resumeDispatcher()
        Truth.assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
    }


}