package moe.pyropix.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import moe.pyropix.data.db.*
import moe.pyropix.data.prefs.AppPrefs
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): PyroDB =
        Room.databaseBuilder(ctx, PyroDB::class.java, "pyro.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideFormulaDao(db: PyroDB): FormulaDao = db.formulaDao()

    @Provides
    fun provideGroupDao(db: PyroDB): GroupDao = db.groupDao()

    @Provides
    @Singleton
    fun providePrefs(@ApplicationContext ctx: Context): AppPrefs = AppPrefs(ctx)
}
