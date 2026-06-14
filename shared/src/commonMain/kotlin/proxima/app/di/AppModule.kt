package proxima.app.di

import org.koin.core.context.startKoin
import org.koin.dsl.module
import proxima.app.data.MainStore

val appModule = module {
    single { MainStore() }
}

private val koinInit by lazy { startKoin { modules(appModule) } }

fun initKoin() {
    koinInit
}
