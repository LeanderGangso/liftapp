package com.patrykandpatryk.liftapp.functionality.database.routine

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.routine.Routine
import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseNames
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao,
    private val routineMapper: RoutineMapper,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : RoutineRepository {

    override fun getRoutinesWithNames(): Flow<List<RoutineWithExerciseNames>> =
        routineDao
            .getRoutinesWithExerciseNames()
            .map(routineMapper::toDomain)
            .flowOn(dispatcher)

    override fun getRoutineWithExercises(routineId: Long): Flow<RoutineWithExercises?> =
        combine(
            routineDao.getRoutine(routineId = routineId),
            routineDao.getRoutineExercises(routineId = routineId),
        ) { nullableRoutine, exercisesWithGoals ->
            nullableRoutine?.let { routine ->
                routineMapper.toDomain(routine, exercisesWithGoals)
            }
        }

    override suspend fun upsert(
        routine: Routine,
        exerciseIds: List<Long>,
    ): Long = withContext(dispatcher + NonCancellable) {
        var routineId = routineDao.upsert(routine = routine.toEntity())

        routineId = routineId.takeIf { it > 0 } ?: routine.id

        upsertOrderedExercises(routineId, exerciseIds)

        routineDao.deleteExerciseWithRoutinesNotIn(routineId, exerciseIds)

        routineId
    }

    override suspend fun reorderExercises(routineId: Long, exerciseIds: List<Long>) {
        upsertOrderedExercises(routineId, exerciseIds)
    }

    private suspend fun upsertOrderedExercises(routineId: Long, exerciseIds: List<Long>) {
        exerciseIds.mapIndexed { index, id ->
            ExerciseWithRoutineEntity(
                routineId = routineId,
                exerciseId = id,
                orderIndex = index,
            )
        }.also { exerciseWithRoutineEntities -> routineDao.upsert(exerciseWithRoutineEntities) }
    }

    override suspend fun delete(routineId: Long) {
        routineDao.delete(routineId)
    }

    override suspend fun deleteExerciseWithRoutine(routineId: Long, exerciseId: Long) {
        routineDao.deleteExerciseWithRoutine(routineId, exerciseId)
    }
}
