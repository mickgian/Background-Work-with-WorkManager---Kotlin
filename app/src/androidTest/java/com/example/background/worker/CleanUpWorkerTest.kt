/* Copyright 2020 Google LLC.
   SPDX-License-Identifier: Apache-2.0 */

package com.example.background.worker

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import com.example.background.workers.CleanUpWorker
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test

class CleanUpWorkerTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var wmRule = WorkManagerTestRule()

    @Test
    fun testCleanupWork() {
        val testUri = copyFileFromTestToTargetCtx(
                wmRule.testContext, wmRule.targetContext, "test_image.png")
        assertThat(uriFileExists(wmRule.targetContext, testUri.toString()), `is`(true))

        // Create request
        val request = OneTimeWorkRequestBuilder<CleanUpWorker>().build()

        // Enqueue and wait for result. This also runs the Worker synchronously
        // because we are using a SynchronousExecutor.
        wmRule.workManager.enqueue(request).result.get()
        // Get WorkInfo
        val workInfo = wmRule.workManager.getWorkInfoById(request.id).get()

        // Assert
        assertThat(uriFileExists(wmRule.targetContext, testUri.toString()), `is`(false))
        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
    }
}