package com.ritesh.imagetopdf.di

import android.content.Context
import androidx.room.Room
import com.ritesh.imagetopdf.R
import com.ritesh.imagetopdf.data.AppPref
import com.ritesh.imagetopdf.data.PdfDatabase
import com.ritesh.imagetopdf.data.PdfRepositoryImpl
import com.ritesh.imagetopdf.domain.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providePdfDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        PdfDatabase::class.java,
        PdfDatabase.DB_NAME
    ).build()

    @Provides
    @Singleton
    fun providePdfRepository(
        db: PdfDatabase
    ) = PdfRepositoryImpl(db.pdfDao)

    @Provides
    @Singleton
    fun provideAppPreferences(
        @ApplicationContext context: Context
    ) : AppPref {
        val preferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        return AppPref(preferences)
    }

    @Provides
    @Singleton
    fun providePdfUseCases(
        repository: PdfRepositoryImpl,
        @ApplicationContext context: Context
    ) = PdfUseCases(
        deletePdf = DeletePdf(repository),
        getPdf = GetPdf(repository),
        createPdf = CreatePdf(repository, context.contentResolver),
        getAllPdfs = GetAllPdfs(repository),
        renamePdf = RenamePdf(repository)
    )
}