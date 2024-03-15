package com.mifos.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.compose.utility.PreferenceManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * @author pratyush
 * @since 15/3/24
 */

class PasscodeViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {

    private val _onPasscodeConfirmed = MutableSharedFlow<String>()
    private val _onPasscodeRejected = MutableSharedFlow<Unit>()
    private val _onPassCodeReceive = MutableSharedFlow<String>()

    private val _activeStep = MutableStateFlow(Step.Create)
    private val _filledDots = MutableStateFlow(0)

    private var createPasscode: StringBuilder = StringBuilder()
    private var confirmPasscode: StringBuilder = StringBuilder()

    val onPasscodeConfirmed = _onPasscodeConfirmed.asSharedFlow()
    val onPasscodeRejected = _onPasscodeRejected.asSharedFlow()
    val onPassCodeReceive = _onPassCodeReceive.asSharedFlow()


    val activeStep = _activeStep.asStateFlow()
    val filledDots = _filledDots.asStateFlow()

    private var _isPasscodeAlreadySet = mutableStateOf(preferenceManager.hasPasscode)
    var isPasscodeAlreadySet: Boolean
        get() = _isPasscodeAlreadySet.value
        set(value) {
            _isPasscodeAlreadySet.value = value
        }

    init {
        resetData()
    }

    private fun emitActiveStep(activeStep: Step) = viewModelScope.launch {
        _activeStep.emit(activeStep)
    }

    private fun emitFilledDots(filledDots: Int) = viewModelScope.launch {
        _filledDots.emit(filledDots)
    }

    private fun emitOnPasscodeConfirmed(confirmPassword: String) = viewModelScope.launch {
        _onPasscodeConfirmed.emit(confirmPassword)
    }

    private fun emitOnPasscodeRejected() = viewModelScope.launch {
        _onPasscodeRejected.emit(Unit)
    }

    private fun emitOnPasscodeReceive(receivedPasscode: String) = viewModelScope.launch {
        _onPassCodeReceive.emit(receivedPasscode)
    }

    private fun resetData() {
        emitActiveStep(Step.Create)
        emitFilledDots(0)

        createPasscode.clear()
        confirmPasscode.clear()
    }

    fun enterKey(key: String) {
        if (_filledDots.value >= PASSCODE_LENGTH) {
            return
        }

        val currentPasscode = if (_activeStep.value == Step.Create) createPasscode else confirmPasscode
        currentPasscode.append(key)
        emitFilledDots(currentPasscode.length)

        if (_filledDots.value == PASSCODE_LENGTH) {
            if (_isPasscodeAlreadySet.value) {
                if (preferenceManager.getSavedPasscode() == createPasscode.toString()) {
                    emitOnPasscodeConfirmed(createPasscode.toString())
                    createPasscode.clear()
                } else {
                    emitOnPasscodeRejected()
                    // logic for retires can be written here
                }
            } else if (_activeStep.value == Step.Create) {
                emitActiveStep(Step.Confirm)
                emitFilledDots(0)
            } else {
                if (createPasscode.toString() == confirmPasscode.toString()) {
                    emitOnPasscodeConfirmed(confirmPasscode.toString())
                    preferenceManager.savePasscode(confirmPasscode.toString())
                    _isPasscodeAlreadySet.value = true
                    resetData()
                } else {
                    emitOnPasscodeRejected()
                    resetData()
                }
            }
        }
    }

    fun deleteKey() {
        _filledDots.tryEmit(
            if (_activeStep.value == Step.Create) {
                if (createPasscode.isNotEmpty()) {
                    createPasscode.deleteAt(createPasscode.length - 1)
                }
                createPasscode.length
            } else {
                if (confirmPasscode.isNotEmpty()) {
                    confirmPasscode.deleteAt(confirmPasscode.length - 1)
                }
                confirmPasscode.length
            }
        )
    }

    fun deleteAllKeys() {
        if (_activeStep.value == Step.Create) {
            createPasscode.clear()
        } else {
            confirmPasscode.clear()
        }

        emitFilledDots(0)
    }

    fun restart() = resetData()

    enum class Step(var index: Int) {
        Create(0),
        Confirm(1)
    }

    companion object {

        const val STEPS_COUNT = 2
        const val PASSCODE_LENGTH = 4
    }
}