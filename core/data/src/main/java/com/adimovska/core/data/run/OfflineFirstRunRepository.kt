package com.adimovska.core.data.run

import com.adimovska.core.data.networking.get
import com.adimovska.core.domain.run.LocalRunDataSource
import com.adimovska.core.domain.run.RemoteRunDataSource
import com.adimovska.core.domain.run.Run
import com.adimovska.core.domain.run.RunId
import com.adimovska.core.domain.run.RunRepository
import com.adimovska.core.domain.util.DataError
import com.adimovska.core.domain.util.EmptyResult
import com.adimovska.core.domain.util.Result
import com.adimovska.core.domain.util.asEmptyDataResult
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProviders
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope,
    private val client: HttpClient
) : RunRepository {

    override fun getRuns(): Flow<List<Run>> {
        return localRunDataSource.getRuns()
    }

    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when (val result = remoteRunDataSource.getRuns()) {
            is Result.Error -> result.asEmptyDataResult()
            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRuns(result.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError> {

        //insert locally
        val localResult = localRunDataSource.upsertRun(run)
        if (localResult !is Result.Success) {
            return localResult.asEmptyDataResult()
        }

        //insert remote
        val runWithId = run.copy(id = localResult.data)
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithId,
            mapPicture = mapPicture
        )

        return when (remoteResult) {
            is Result.Error -> {
                //todo change
                Result.Success(Unit)
            }

            is Result.Success -> { // we do it again to update with the remote url
                applicationScope.async {
                    localRunDataSource.upsertRun(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteRun(id: RunId) {
        localRunDataSource.deleteRun(id)


        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()

    }

    override suspend fun deleteAllRuns() {
        localRunDataSource.deleteAllRuns()
    }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        val result = client.get<Unit>(
            route = "/logout"
        ).asEmptyDataResult()

        client.authProviders.filterIsInstance<BearerAuthProvider>()
            .firstOrNull()
            ?.clearToken()

        return result
    }
}