import com.adimovska.core.domain.run.SyncRunScheduler
import com.adimovska.run.data.CreateRunWorker
import com.adimovska.run.data.DeleteRunWorker
import com.adimovska.run.data.FetchRunsWorker
import com.adimovska.run.data.SyncRunWorkerScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)

    singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
}