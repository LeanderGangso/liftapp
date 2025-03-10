package com.patrykandpatryk.liftapp.domain.exercise

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class DeleteExerciseUseCase
@Inject
constructor(
    private val repository: ExerciseRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(exerciseId: Long) =
        withContext(dispatcher + NonCancellable) { repository.delete(exerciseId) }
}
