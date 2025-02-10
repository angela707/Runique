
import com.adimovska.run.data.CreateRunWorker
import com.adimovska.run.data.DeleteRunWorker
import com.adimovska.run.data.FetchRunsWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)
}