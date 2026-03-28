package com.mahi.weatherapp.common

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * A single launcher that can either:
 * \- keep the previous active job \(do not launch a new one\), or
 * \- cancel the previous job and launch a new one.
 *
 * @param jobRef A reference to the existing Job.
 * @param cancelPrevious If `true`, cancels [jobRef] before launching.
 * If `false`, returns [jobRef] when it is active; otherwise launches a new job.
 * @param block The suspend block to execute.
 * @return The active previous Job \(when not cancelling and still active\) or the new launched Job.
 */
fun CoroutineScope.launchWithPrevious(
    jobRef: Job?,
    cancelPrevious: Boolean = false,
    block: suspend CoroutineScope.() -> Unit
): Job {
    Log.d("c", "cancelPrevious: $cancelPrevious, jobRef isActive: ${jobRef?.isActive?:"jobRef is null"}")
    if (cancelPrevious) {
        jobRef?.cancel()
        return launch(block = block)
    }

    return if (jobRef?.isActive == true) {
        jobRef
    } else {
        launch(block = block)
    }
}

/**
 * A "join previous or run" launcher.
 * If the job in [jobRef] is active, it does nothing. Otherwise, it launches the new block.
 *
 * @param jobRef A reference to the Job that should be checked.
 * @param block The suspend block to execute if no previous job is active.
 * @return The new Job if it was launched, or the existing active Job.
 */
fun CoroutineScope.launchJoinPrevious(
    jobRef: Job?,
    block: suspend CoroutineScope.() -> Unit
): Job {
    if (jobRef?.isActive == true) {
        println("launchJoinPrevious: Previous job is active. New job not launched.")
        return jobRef
    }
    return this.launch(block = block)
}




/**
 * A "cancel previous then run" launcher.
 * Cancels the previous job held in [jobRef] before launching the new block.
 *
 * @param jobRef A reference to the Job that should be cancelled.
 * @param block The suspend block to execute.
 * @return The new Job instance created by the launch builder.
 */
fun CoroutineScope.launchCancelPrevious(
    jobRef: Job?,
    block: suspend CoroutineScope.() -> Unit
): Job {
    jobRef?.cancel()
    return this.launch(block = block)
}


/**
 * Launches a new coroutine that either waits for the previous [Job] or cancels it,
 * then runs [block].
 *
 * \- If [force] is `false`, it calls `join()` on [previous] before running [block].
 * \- If [force] is `true`, it cancels [previous] (if present) and runs [block] immediately.
 *
 * Returns the newly launched [Job].
 */

inline fun CoroutineScope.joinPreviousOrRun(previous: Job?, force: Boolean, crossinline block: suspend () -> Unit): Job {
    return launch {
        if (!force) {
            previous?.join()
        } else {
            previous?.cancel()
        }
        block()
    }
}